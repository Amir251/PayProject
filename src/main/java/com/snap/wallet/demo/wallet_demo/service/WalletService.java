package com.snap.wallet.demo.wallet_demo.service;

import com.snap.wallet.demo.wallet_demo.model.Wallet;

import java.math.BigDecimal;

public interface WalletService {

    void increaseUserBalance(String userEmail, BigDecimal newBalance);

    void transferMoney(String destEmailAccount, BigDecimal transferAmount);

    Wallet findAdminWallet();
}
