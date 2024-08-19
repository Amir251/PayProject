package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.constant.ExceptionMessageCode;
import com.snap.wallet.demo.wallet_demo.domain.RequestContext;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionStatus;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionType;
import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.model.TransactionEntity;
import com.snap.wallet.demo.wallet_demo.model.UserEntity;
import com.snap.wallet.demo.wallet_demo.model.Wallet;
import com.snap.wallet.demo.wallet_demo.repository.TransactionRepository;
import com.snap.wallet.demo.wallet_demo.repository.WalletRepository;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import com.snap.wallet.demo.wallet_demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    private final UserService userService;

    @Override
    @Transactional
    public void increaseUserBalance(String userEmail, BigDecimal newBalance) {
        Wallet adminWallet = findAdminWallet();
        Wallet destWallet = getWalletByEmail(userEmail);
        destWallet.setBalance(newBalance);
        saveTransaction(newBalance, adminWallet, destWallet, "Charged Wallet By Admin!");
        walletRepository.save(destWallet);
    }

    @Override
    @Transactional
    public void transferMoney(String destEmailAccount, BigDecimal transferAmount) {
        UserEntity currentUser = userService.findById(RequestContext.getUserId());
        Wallet sourceWallet = getWalletByEmail(currentUser.getEmail());
        Wallet destWallet = getWalletByEmail(destEmailAccount);

        validateSufficientBalance(sourceWallet, transferAmount);

        updateBalances(sourceWallet, destWallet, transferAmount);

        saveTransaction(transferAmount, sourceWallet, destWallet,
                String.format("Transfer From %s To %s", sourceWallet.getUser().getEmail(), destEmailAccount));

        walletRepository.save(sourceWallet);
        walletRepository.save(destWallet);
    }

    @Override
    public Wallet findAdminWallet() {
        String ADMIN_WALLET = "ADMIN_WALLET";
        return walletRepository.findByAccountNumber(ADMIN_WALLET).orElseThrow(() -> new ApiException(ExceptionMessageCode.WALLET_NOT_FOUND));
    }

    private Wallet getWalletByEmail(String email) {
        return walletRepository.findByUserEmail(email)
                .orElseThrow(() -> new ApiException(ExceptionMessageCode.CURRENT_USER_WALLET_NOT_FOUND));
    }

    private void validateSufficientBalance(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException(ExceptionMessageCode.BALANCE_NOT_ENOUGH);
        }
    }

    private void updateBalances(Wallet sourceWallet, Wallet destWallet, BigDecimal amount) {
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));
        destWallet.setBalance(destWallet.getBalance().add(amount));
    }

    private void saveTransaction(BigDecimal newBalance, Wallet sourceWallet, Wallet destWallet, String description) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setSourceWallet(sourceWallet);
        transactionEntity.setDestinationWallet(destWallet);
        transactionEntity.setStatus(TransactionStatus.SUCCESS);
        transactionEntity.setAmount(newBalance);
        transactionEntity.setTransactionType(TransactionType.TRANSFER);
        transactionEntity.setDescription(description);
        transactionEntity.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transactionEntity);
    }
}
