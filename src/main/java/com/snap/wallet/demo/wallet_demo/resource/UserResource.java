package com.snap.wallet.demo.wallet_demo.resource;

import com.snap.wallet.demo.wallet_demo.domain.Response;
import com.snap.wallet.demo.wallet_demo.dtorequest.LoginRequest;
import com.snap.wallet.demo.wallet_demo.dtorequest.UserRequest;
import com.snap.wallet.demo.wallet_demo.service.JwtService;
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
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.snap.wallet.demo.wallet_demo.util.RequestUtil.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/users")
@Tag(name = "User Resource", description = "Operations related to User management, access for all user")
public class UserResource {
    private final UserService userService;

    @Operation(summary = "Register a new user", description = "This endpoint allows the registration of a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
    })
    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(@RequestBody @Valid UserRequest userRequest, HttpServletRequest request) {
        userService.createUser(userRequest);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Account created. please check your email for enable account", HttpStatus.CREATED));
    }

    @Operation(
            summary = "Verify User Account",
            description = "This endpoint verifies the user's account using a verification key. It should be called via the verification link sent to the user's email. Directly invoking this endpoint without the proper verification link may result in an unsuccessful verification."
    )
    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifiedAccount(@RequestParam("key") String key, HttpServletRequest request) {
        userService.verifiedUserByKey(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account Verified", HttpStatus.OK));
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "This endpoint handles user login.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User logged in successfully"),})
    public void login(@RequestBody LoginRequest loginRequest) {
    }
    @Operation(summary = "Logout user", description = "This endpoint handles user logout.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged out successfully")
    })
    @PostMapping("/logout")
    public void logout() {
    }

    private URI getUri() {
        return URI.create("");
    }
}
