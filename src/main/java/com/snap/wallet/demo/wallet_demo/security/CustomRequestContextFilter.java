package com.snap.wallet.demo.wallet_demo.security;

import com.snap.wallet.demo.wallet_demo.domain.RequestContext;
import com.snap.wallet.demo.wallet_demo.domain.TokenData;
import com.snap.wallet.demo.wallet_demo.enumeration.TokenType;
import com.snap.wallet.demo.wallet_demo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class CustomRequestContextFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            RequestContext.start();

            String token = jwtService.extractToken(request, TokenType.ACCESS.getValue()).orElse(null);

            if (token != null) {
                Long userId = jwtService.getTokenData(token, TokenData::getUser).getId();
                RequestContext.setUserId(userId);
            }

            filterChain.doFilter(request, response);
        } finally {
            RequestContext.start();
        }
    }
}
