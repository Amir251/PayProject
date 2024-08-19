package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.constant.ExceptionMessageCode;
import com.snap.wallet.demo.wallet_demo.domain.RequestContext;
import com.snap.wallet.demo.wallet_demo.dto.ProductDto;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionStatus;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionType;
import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.model.*;
import com.snap.wallet.demo.wallet_demo.repository.ProductRepository;
import com.snap.wallet.demo.wallet_demo.repository.PurchaseRepository;
import com.snap.wallet.demo.wallet_demo.repository.TransactionRepository;
import com.snap.wallet.demo.wallet_demo.repository.WalletRepository;
import com.snap.wallet.demo.wallet_demo.service.ProductService;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import com.snap.wallet.demo.wallet_demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserService userService;
    private final WalletRepository walletRepository;

    private final TransactionRepository transactionRepository;

    private final PurchaseRepository purchaseRepository;
    private final WalletService walletService;

    @Override
    public void saveProduct(ProductDto dto) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(dto, productEntity);
        productRepository.save(productEntity);
    }

    @Override
    public void updateProduct(ProductDto dto) {
        ProductEntity productEntity = getProductEntity.apply(dto.id());
        BeanUtils.copyProperties(dto, productEntity);
        productRepository.save(productEntity);
    }


    @Override
    public List<ProductDto> findAllProducts() {
        return productRepository.findAll().stream().map(productEntity -> new ProductDto(productEntity.getId(), productEntity.getName(), productEntity.getDescription(), productEntity.getPrice(), productEntity.getStock())).toList();
    }

    @Override
    @Transactional
    public void buyProduct(Long productId, int quantity) {
        Wallet wallet = null;
        try {
            UserEntity user = userService.findById(RequestContext.getUserId());
            wallet = getWalletByEmail.apply(user.getEmail());
            ProductEntity product = getProductEntity.apply(productId);
            if (quantity > product.getStock()) {
                throw new ApiException(ExceptionMessageCode.REQUESTED_QUANTITY_EXCEEDS_AVAILABLE_STOCK);
            }
            BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));

            if (totalAmount.compareTo(wallet.getBalance()) > 0) {
                throw new ApiException(ExceptionMessageCode.THE_TOTAL_AMOUNT_EXCEEDS_YOUR_AVAILABLE_BALANCE);
            }
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            wallet.setBalance(wallet.getBalance().subtract(totalAmount));
            walletRepository.save(wallet);
            saveTransaction(totalAmount, wallet, walletService.findAdminWallet(), user.getEmail() + " bought a product with price: " + totalAmount, TransactionStatus.SUCCESS);
            PurchaseEntity purchase = getPurchaseEntity(quantity, user, product, totalAmount);
            purchaseRepository.save(purchase);
        } catch (Exception e) {
            try {
                if (wallet != null) {
                    logFailedTransaction(BigDecimal.ZERO, wallet, walletService.findAdminWallet(), "Failed transaction: " + e.getMessage());
                } else {
                    log.error("Wallet is null, failed to log failed transaction.");
                }
            } catch (Exception ex) {
                log.error("Failed to log failed transaction: " + ex.getMessage());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailedTransaction(BigDecimal amount, Wallet sourceWallet, Wallet destWallet, String description) {
        saveTransaction(amount, sourceWallet, destWallet, description, TransactionStatus.FAILED);
    }

    private PurchaseEntity getPurchaseEntity(int quantity, UserEntity user, ProductEntity product, BigDecimal totalAmount) {
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setUser(user);
        purchase.setQuantity(quantity);
        purchase.setProduct(product);
        purchase.setTotalPrice(totalAmount);
        return purchase;
    }

    private final Function<String, Wallet> getWalletByEmail = email -> walletRepository.findByUserEmailWithLock(email)
            .orElseThrow(() -> new ApiException(ExceptionMessageCode.CURRENT_USER_WALLET_NOT_FOUND));


    private final Function<Long, ProductEntity> getProductEntity = productId ->
            productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> new ApiException(ExceptionMessageCode.PRODUCT_NOT_FOUND));


    private void saveTransaction(BigDecimal totalAmount, Wallet sourceWallet, Wallet destWallet, String description, TransactionStatus status) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setSourceWallet(sourceWallet);
        transactionEntity.setDestinationWallet(destWallet);
        transactionEntity.setStatus(status);
        transactionEntity.setAmount(totalAmount);
        transactionEntity.setTransactionType(TransactionType.PURCHASE);
        transactionEntity.setDescription(description);
        transactionEntity.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transactionEntity);
    }

}
