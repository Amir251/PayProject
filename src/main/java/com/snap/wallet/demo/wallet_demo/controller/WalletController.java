package com.snap.wallet.demo.wallet_demo.controller;

import com.snap.wallet.demo.wallet_demo.domain.Response;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import com.snap.wallet.demo.wallet_demo.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

import static com.snap.wallet.demo.wallet_demo.util.RequestUtil.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequestMapping("api/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet Management", description = "Operations related to managing user wallets and balances.")
public class WalletController {

    private final UserService userService;
    private final WalletService walletService;

    @Operation(summary = "Load All User Emails (Only ADMIN)", description = "This endpoint allows an admin to retrieve a list of all user emails.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User emails loaded successfully."),
            @ApiResponse(responseCode = "403", description = "Access denied. The user does not have the required role."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/loadUserEmails")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> loadUserEmails(HttpServletRequest request) {
        return ResponseEntity.ok(getResponse(request, Map.of("User Emails", userService.findAllUsersEmail()), "emails loaded!", HttpStatus.OK));
    }

    @Operation(summary = "Increase User Wallet Balance (Only ADMIN)", description = "This endpoint allows an admin to increase the balance of a user's wallet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User wallet balance increased successfully."),
            @ApiResponse(responseCode = "403", description = "Access denied. The user does not have the required role."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/increaseUserBalance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> increaseUserBalance(@Parameter(description = "The email of the user whose balance is to be increased.")
                                                        @RequestParam String userEmail,
                                                        @Parameter(description = "The amount by which the user's balance is to be increased.")
                                                        @RequestParam BigDecimal newBalance,
                                                        HttpServletRequest request) {
        walletService.increaseUserBalance(userEmail, newBalance);
        return ResponseEntity.ok(getResponse(request, emptyMap(), "Wallet Balance Increased!", HttpStatus.OK));
    }

    @Operation(summary = "Transfer Money Between Accounts", description = "This endpoint allows a user to transfer money to another user's account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer completed successfully."),
            @ApiResponse(responseCode = "403", description = "Access denied. The user does not have the required role."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/transferMoneyBetweenTwoAccount")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Response> transferMoneyBetweenTwoAccount(@Parameter(description = "The email of the account to which the money will be transferred.")
                                                                   @RequestParam String destEmailAccount,
                                                                   @Parameter(description = "The amount of money to be transferred.")
                                                                   @RequestParam BigDecimal transferAmount,
                                                                   HttpServletRequest request) {
        walletService.transferMoney(destEmailAccount, transferAmount);
        return ResponseEntity.ok(getResponse(request, emptyMap(), "Transfer Completed", HttpStatus.OK));
    }

    @Operation(summary = "Show User Wallet Balance", description = "This endpoint allows a user to view their current wallet balance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User wallet balance loaded successfully."),
            @ApiResponse(responseCode = "403", description = "Access denied. The user does not have the required role."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/showBalance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Response> showBalance(HttpServletRequest request) {
        return ResponseEntity.ok(getResponse(request, Map.of("balance", walletService.loadBalanceData()), "Balance Loaded!", HttpStatus.OK));
    }
}
