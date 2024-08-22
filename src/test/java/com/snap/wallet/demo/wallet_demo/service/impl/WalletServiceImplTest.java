package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.constant.ExceptionMessageCode;
import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.model.Wallet;
import com.snap.wallet.demo.wallet_demo.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    void findAdminWallet_Success() {
        String ADMIN_WALLET = "ADMIN_WALLET";
        Wallet adminWallet = new Wallet();
        adminWallet.setAccountNumber(ADMIN_WALLET);

        when(walletRepository.findByAccountNumber(ADMIN_WALLET)).thenReturn(Optional.of(adminWallet));

        Wallet result = assertDoesNotThrow(() -> walletService.findAdminWallet());
        assertNotNull(result);
        assertEquals(ADMIN_WALLET, result.getAccountNumber());
    }

    @Test
    void findAdminWallet_NotFound() {
        String ADMIN_WALLET = "ADMIN_WALLET";

        when(walletRepository.findByAccountNumber(ADMIN_WALLET)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> walletService.findAdminWallet());
        assertEquals(ExceptionMessageCode.WALLET_NOT_FOUND, exception.getMessage());
    }
}