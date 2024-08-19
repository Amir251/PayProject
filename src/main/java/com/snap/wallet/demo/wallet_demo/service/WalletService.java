package com.snap.wallet.demo.wallet_demo.service;

import java.math.BigDecimal;

public interface WalletService {

    void increaseUserBalance(String userEmail, BigDecimal newBalance);
}
