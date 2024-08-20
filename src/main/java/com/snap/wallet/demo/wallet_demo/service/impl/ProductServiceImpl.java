package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.constant.ExceptionMessageCode;
import com.snap.wallet.demo.wallet_demo.domain.RequestContext;
import com.snap.wallet.demo.wallet_demo.dto.ProductDto;
import com.snap.wallet.demo.wallet_demo.enumeration.EventType;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionStatus;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionType;
import com.snap.wallet.demo.wallet_demo.event.UserEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private final ApplicationEventPublisher publisher;

    @Override
    public void saveProduct(ProductDto dto) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(dto, productEntity);
        productRepository.save(productEntity);
    }

    @Override
    public void updateProduct(ProductDto dto) {
        ProductEntity productEntity = getProductEntity(dto.id());
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
        StringBuilder message = new StringBuilder();
        UserEntity user = null;
        try {
            user = userService.findById(RequestContext.getUserId());
            wallet = getWalletByEmail(user.getEmail());
            ProductEntity product = getProductEntity(productId);
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
            message.append(String.format("Dear %s,\n\n", user.getFirstName()))
                    .append("Your purchase was successful.\n\n")
                    .append("Details of your purchase:\n")
                    .append(String.format("Product: %s\n", product.getName()))
                    .append(String.format("Quantity: %d\n", quantity))
                    .append(String.format("Total Price: %s\n", totalAmount))
                    .append("\nThank you for your purchase!");
        } catch (Exception e) {
            try {
                if (user != null && wallet != null) {
                    logFailedTransaction(BigDecimal.ZERO, wallet, walletService.findAdminWallet(), "Failed transaction: " + e.getMessage());
                    message.append(String.format("Dear %s,\n\n", user.getFirstName()))
                            .append("Unfortunately, your purchase was not successful.\n\n")
                            .append("If any amount was deducted from your wallet, it will be refunded to your account shortly.\n")
                            .append("We apologize for the inconvenience.");
                } else {
                    log.error("Wallet is null, failed to log failed transaction.");
                }
            } catch (Exception ex) {
                log.error("Failed to log failed transaction: " + ex.getMessage());
            }
        } finally {
            if (message.length() > 0 && user != null)
                publisher.publishEvent(new UserEvent(user, EventType.PURCHASE, null, message.toString()));
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

    public Wallet getWalletByEmail(String email) {
        return walletRepository.findByUserEmailWithLock(email)
                .orElseThrow(() -> new ApiException(ExceptionMessageCode.CURRENT_USER_WALLET_NOT_FOUND));
    }

    public ProductEntity getProductEntity(Long productId) {
        return productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new ApiException(ExceptionMessageCode.PRODUCT_NOT_FOUND));
    }


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
