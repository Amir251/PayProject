package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.constant.ExceptionMessageCode;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionStatus;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionType;
import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.model.TransactionEntity;
import com.snap.wallet.demo.wallet_demo.model.Wallet;
import com.snap.wallet.demo.wallet_demo.repository.TransactionRepository;
import com.snap.wallet.demo.wallet_demo.repository.WalletRepository;
import com.snap.wallet.demo.wallet_demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void increaseUserBalance(String userEmail, BigDecimal newBalance) {
        String ADMIN_WALLET = "ADMIN_WALLET";
        Wallet adminWallet = walletRepository.findByAccountNumber(ADMIN_WALLET).orElseThrow(() -> new ApiException(ExceptionMessageCode.WALLET_NOT_FOUND));
        Wallet destWallet = walletRepository.findByUserEmail(userEmail).orElseThrow(() -> new ApiException(ExceptionMessageCode.WALLET_NOT_FOUND));
        destWallet.setBalance(newBalance);
        saveTransaction(newBalance, adminWallet, destWallet);
        walletRepository.save(destWallet);
    }

    private void saveTransaction(BigDecimal newBalance, Wallet adminWallet, Wallet destWallet) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setSourceWallet(adminWallet);
        transactionEntity.setDestinationWallet(destWallet);
        transactionEntity.setStatus(TransactionStatus.SUCCESS);
        transactionEntity.setAmount(newBalance);
        transactionEntity.setTransactionType(TransactionType.TRANSFER);
        transactionEntity.setDescription("Charged Wallet By Admin!");
        transactionEntity.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transactionEntity);
    }
}
