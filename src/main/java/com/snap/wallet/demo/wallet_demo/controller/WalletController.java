package com.snap.wallet.demo.wallet_demo.controller;

import com.snap.wallet.demo.wallet_demo.domain.Response;
import com.snap.wallet.demo.wallet_demo.service.JwtService;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import com.snap.wallet.demo.wallet_demo.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.snap.wallet.demo.wallet_demo.util.RequestUtil.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequestMapping("api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final UserService userService;
    private final WalletService walletService;

    @GetMapping("/loadUserEmails")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> loadUserEmails(HttpServletRequest request) {
        return ResponseEntity.ok(getResponse(request, Map.of("User Emails", userService.findAllUsersEmail()), "emails loaded!", HttpStatus.OK));
    }

    @PostMapping("/increaseUserBalance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> increaseUserBalance(String userEmail, BigDecimal newBalance, HttpServletRequest request) {
        walletService.increaseUserBalance(userEmail, newBalance);
        return ResponseEntity.ok(getResponse(request, emptyMap(), "Wallet Balance Increased!", HttpStatus.OK));
    }
}
