package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.constant.ExceptionMessageCode;
import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.model.Wallet;
import com.snap.wallet.demo.wallet_demo.repository.WalletRepository;
import com.snap.wallet.demo.wallet_demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Override
    public void increaseUserBalance(String userEmail, BigDecimal newBalance) {
        Wallet wallet = walletRepository.findByUserEmail(userEmail).orElseThrow(() -> new ApiException(ExceptionMessageCode.WALLET_NOT_FOUND));
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
    }
}
