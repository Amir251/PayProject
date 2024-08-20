package com.snap.wallet.demo.wallet_demo.controller;

import com.snap.wallet.demo.wallet_demo.domain.RequestContext;
import com.snap.wallet.demo.wallet_demo.domain.Response;
import com.snap.wallet.demo.wallet_demo.dtorequest.UserRequest;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.snap.wallet.demo.wallet_demo.util.RequestUtil.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
@Tag(name = "User Dashboard", description = "Endpoints for managing user information and transactions.")
public class UserController {

    private final UserService userService;

    @PatchMapping("/update")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update User Information", description = "This endpoint allows a user to update their personal information. Only users with the 'USER' role can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "403", description = "Access denied. The user does not have the required role."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<Response> update(@RequestBody @Valid UserRequest userRequest, HttpServletRequest request) {
        userService.updateUser(userRequest, RequestContext.getUserId());
        return ResponseEntity.ok(getResponse(request, emptyMap(), "User updated!", HttpStatus.OK));
    }

    @Operation(summary = "Get User Transactions Report", description = "This endpoint retrieves a report of all transactions associated with the authenticated user. Only users with the 'USER' role can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions loaded successfully."),
            @ApiResponse(responseCode = "403", description = "Access denied. The user does not have the required role."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/showReportUserTransactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Response> showReportUserTransactions(HttpServletRequest request) {
        return ResponseEntity.ok(getResponse(request, Map.of("transactions", userService.loadUserTransactions()), "Transactions Loaded!", HttpStatus.OK));
    }
}
