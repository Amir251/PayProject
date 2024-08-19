package com.snap.wallet.demo.wallet_demo.security;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snap.wallet.demo.wallet_demo.domain.ApiAuthentication;
import com.snap.wallet.demo.wallet_demo.domain.Response;
import com.snap.wallet.demo.wallet_demo.dto.User;
import com.snap.wallet.demo.wallet_demo.dtorequest.LoginRequest;
import com.snap.wallet.demo.wallet_demo.enumeration.TokenType;
import com.snap.wallet.demo.wallet_demo.model.UserEntity;
import com.snap.wallet.demo.wallet_demo.service.JwtService;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.snap.wallet.demo.wallet_demo.util.RequestUtil.getResponse;
import static com.snap.wallet.demo.wallet_demo.util.RequestUtil.handleErrorResponse;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        super(new AntPathRequestMatcher("/api/users/login", POST.name()), authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true).readValue(request.getInputStream(), LoginRequest.class);
            String email = loadMailByPhone(loginRequest.getEmail());
            if (email == null)
                loginRequest.setEmail(loginRequest.getEmail());
            ApiAuthentication authenticated = ApiAuthentication.unauthenticated(loginRequest.getEmail(), loginRequest.getPassword());
            return getAuthenticationManager().authenticate(authenticated);
        } catch (Exception e) {
            log.error(e.getMessage());
            handleErrorResponse(request, response, e);
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authenticated) throws IOException {
        User user = (User) authenticated.getPrincipal();
        Response httpResponse = sendResponse(request, response, user);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(OK.value());
        ServletOutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();

    }

    private Response sendResponse(HttpServletRequest request, HttpServletResponse response, User user) {
        jwtService.addCookie(response, user, TokenType.ACCESS);
        return getResponse(request, Map.of("user", user), "Logged in success", OK);
    }

    private String loadMailByPhone(String phone) {
        Optional<UserEntity> byPhone = userService.findByPhone(phone);
        return byPhone.map(UserEntity::getEmail).orElse(null);
    }

}
