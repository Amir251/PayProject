package com.snap.wallet.demo.wallet_demo.security;

import com.snap.wallet.demo.wallet_demo.domain.ApiAuthentication;
import com.snap.wallet.demo.wallet_demo.domain.UserPrinciple;
import com.snap.wallet.demo.wallet_demo.dto.User;
import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.model.CredentialEntity;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.snap.wallet.demo.wallet_demo.domain.ApiAuthentication.authenticated;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiAuthentication apiAuthentication = authenticationFunction.apply(authentication);
        User userByEmail = userService.getUserByEmail(apiAuthentication.getEmail());
        if (userByEmail != null) {
            CredentialEntity credentialEntity = userService.getUserCredentialById(userByEmail.getId());
            if (!userByEmail.isCredentialNonExpire()) {
                throw new ApiException("Credential are expired please change password");
            }
            UserPrinciple userPrinciple = new UserPrinciple(userByEmail, credentialEntity);
            validAccount.accept(userPrinciple);
            if (encoder.matches(apiAuthentication.getPassword(), credentialEntity.getPassword())) {
                return authenticated(userByEmail, userPrinciple.getAuthorities());
            } else throw new ApiException("Email or password is incorrect");
        } else {
            throw new ApiException("Unable to authenticate");
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

    private final Function<Authentication, ApiAuthentication> authenticationFunction = authentication -> (ApiAuthentication) authentication;

    private final Consumer<UserPrinciple> validAccount = userPrinciple -> {
        if (!userPrinciple.isAccountNonLocked()) {
            throw new LockedException("Your Account is currently locked");
        }
        if (!userPrinciple.isEnabled()) {
            throw new DisabledException("Your Account is currently disabled");
        }
        if (!userPrinciple.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Your Account is currently expired. please update password ");
        }
        if (!userPrinciple.isAccountNonExpired()) {
            throw new DisabledException("Your Account is currently expired. please contact admin");
        }
    };
}
