package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.constant.ExceptionMessageCode;
import com.snap.wallet.demo.wallet_demo.domain.RequestContext;
import com.snap.wallet.demo.wallet_demo.enumeration.EventType;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionStatus;
import com.snap.wallet.demo.wallet_demo.enumeration.TransactionType;
import com.snap.wallet.demo.wallet_demo.event.UserEvent;
import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.function.TriConsumer;
import com.snap.wallet.demo.wallet_demo.model.TransactionEntity;
import com.snap.wallet.demo.wallet_demo.model.UserEntity;
import com.snap.wallet.demo.wallet_demo.model.Wallet;
import com.snap.wallet.demo.wallet_demo.repository.TransactionRepository;
import com.snap.wallet.demo.wallet_demo.repository.WalletRepository;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import com.snap.wallet.demo.wallet_demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public void increaseUserBalance(String userEmail, BigDecimal newBalance) {
        Wallet adminWallet = null;
        try {
            adminWallet = findAdminWallet();
            Wallet destWallet = getWalletByEmail(userEmail);
            destWallet.setBalance(newBalance);
            saveTransaction(newBalance, adminWallet, destWallet, "Charged Wallet By Admin!", TransactionStatus.SUCCESS);
            walletRepository.save(destWallet);
        } catch (Exception e) {
            try {
                if (adminWallet != null) {
                    logFailedTransaction(BigDecimal.ZERO, adminWallet, adminWallet, e.getMessage());
                } else {
                    log.error("Wallet is null, failed to log failed transaction.");
                }
            } catch (Exception ex) {
                log.error("Failed to log failed transaction: " + ex.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void transferMoney(String destEmailAccount, BigDecimal transferAmount) {
        Wallet sourceWallet = null;
        StringBuilder successMessage = new StringBuilder();
        StringBuilder failMessage = new StringBuilder();
        UserEntity destUser = null;
        UserEntity currentUser = null;
        try {
            currentUser = userService.findById(RequestContext.getUserId());
            sourceWallet = getWalletByEmail(currentUser.getEmail());
            Wallet destWallet = getWalletByEmail(destEmailAccount);
            destUser = destWallet.getUser();
            validateSufficientBalance.accept(sourceWallet, transferAmount);
            updateBalances.accept(sourceWallet, destWallet, transferAmount);
            saveTransaction(transferAmount, sourceWallet, destWallet,
                    String.format("Transfer From %s To %s", sourceWallet.getUser().getEmail(), destEmailAccount), TransactionStatus.SUCCESS);
            walletRepository.save(sourceWallet);
            walletRepository.save(destWallet);
            successMessage.append(String.format("Dear %s,\n\n", destUser.getFirstName()))
                    .append(String.format("You have received a transfer from %s.\n\n", currentUser.getEmail()))
                    .append(String.format("Amount: %s\n", transferAmount))
                    .append(String.format("Your new balance is: %s\n", destWallet.getBalance()))
                    .append("\nThank you for using our service!");

        } catch (Exception e) {
            try {
                if (currentUser != null && sourceWallet != null) {
                    logFailedTransaction(BigDecimal.ZERO, sourceWallet, sourceWallet, e.getMessage());
                    failMessage.append(String.format("Dear %s,\n\n", currentUser.getFirstName()))
                            .append("Unfortunately, your transfer was not successful.\n\n")
                            .append("If any amount was deducted from your wallet, it will be refunded to your account shortly.\n")
                            .append("We apologize for the inconvenience and appreciate your understanding.");
                } else {
                    log.error("Wallet is null, failed to log failed transaction.");
                }
            } catch (Exception ex) {
                log.error("Failed to log failed transaction: " + ex.getMessage());
            }
        } finally {
            if (successMessage.length() > 0 && destUser != null) {
                publisher.publishEvent(new UserEvent(destUser, EventType.TRANSFER, null, successMessage.toString()));
            } else if (failMessage.length() > 0 && currentUser != null) {
                publisher.publishEvent(new UserEvent(currentUser, EventType.TRANSFER, null, failMessage.toString()));
            }
        }
    }

    @Override
    public Wallet findAdminWallet() {
        String ADMIN_WALLET = "ADMIN_WALLET";
        return walletRepository.findByAccountNumber(ADMIN_WALLET).orElseThrow(() -> new ApiException(ExceptionMessageCode.WALLET_NOT_FOUND));
    }

    @Override
    public BigDecimal loadBalanceData() {
        return walletRepository.findByUser_Id(RequestContext.getUserId())
                .orElseThrow(()->new ApiException(ExceptionMessageCode.WALLET_NOT_FOUND)).getBalance();
    }

    public Wallet getWalletByEmail(String email) {
        return walletRepository.findByUserEmail(email)
                .orElseThrow(() -> new ApiException(ExceptionMessageCode.CURRENT_USER_WALLET_NOT_FOUND));
    }


    private final BiConsumer<Wallet, BigDecimal> validateSufficientBalance = (wallet, amount) -> {
        if (wallet.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException(ExceptionMessageCode.BALANCE_NOT_ENOUGH);
        }
    };

    private final TriConsumer<Wallet, Wallet, BigDecimal> updateBalances = (sourceWallet, destWallet, amount) -> {
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));
        destWallet.setBalance(destWallet.getBalance().add(amount));
    };

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailedTransaction(BigDecimal amount, Wallet sourceWallet, Wallet destWallet, String description) {
        saveTransaction(amount, sourceWallet, destWallet, description, TransactionStatus.FAILED);
    }

    private void saveTransaction(BigDecimal newBalance, Wallet sourceWallet, Wallet destWallet, String description, TransactionStatus status) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setSourceWallet(sourceWallet);
        transactionEntity.setDestinationWallet(destWallet);
        transactionEntity.setStatus(status);
        transactionEntity.setAmount(newBalance);
        transactionEntity.setTransactionType(TransactionType.TRANSFER);
        transactionEntity.setDescription(description);
        transactionEntity.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transactionEntity);
    }
}
