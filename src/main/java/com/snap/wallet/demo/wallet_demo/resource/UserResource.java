package com.snap.wallet.demo.wallet_demo.resource;

import com.snap.wallet.demo.wallet_demo.domain.Response;
import com.snap.wallet.demo.wallet_demo.dtorequest.LoginRequest;
import com.snap.wallet.demo.wallet_demo.dtorequest.UserRequest;
import com.snap.wallet.demo.wallet_demo.service.JwtService;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import static com.snap.wallet.demo.wallet_demo.util.RequestUtil.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/users")
public class UserResource {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(@RequestBody @Valid UserRequest userRequest, HttpServletRequest request) {
        userService.createUser(userRequest);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Account created. please check your email for enable account", HttpStatus.CREATED));
    }

    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifiedAccount(@RequestParam("key") String key, HttpServletRequest request) {
        userService.verifiedUserByKey(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account Verified", HttpStatus.OK));
    }

    @GetMapping("/checkAuth")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        Optional<String> jwtToken = jwtService.extractToken(request, "jwtToken");
        if (jwtToken.isPresent() && jwtService.isTokenValid(jwtToken.get())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest) {
    }

    @PostMapping("/logout")
    public void logout() {
    }

    private URI getUri() {
        return URI.create("");
    }
}
