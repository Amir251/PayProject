package com.snap.wallet.demo.wallet_demo.security;

import com.snap.wallet.demo.wallet_demo.constant.ExceptionMessageCode;
import com.snap.wallet.demo.wallet_demo.domain.TokenData;
import com.snap.wallet.demo.wallet_demo.domain.UserPrinciple;
import com.snap.wallet.demo.wallet_demo.dto.User;
import com.snap.wallet.demo.wallet_demo.enumeration.TokenType;
import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.model.CredentialEntity;
import com.snap.wallet.demo.wallet_demo.service.JwtService;
import com.snap.wallet.demo.wallet_demo.service.impl.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> EXCLUDED_URLS = List.of(
            "/api/users/**",
            "/",
            "/swagger-ui/**",
            "/v3/**",
            "/api/products/findAllProducts"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String requestPath = request.getServletPath();
        log.info("----------- " + requestPath + "---------");
        if (EXCLUDED_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath))) {
            chain.doFilter(request, response);
            return;
        }
        Optional<String> token = jwtService.extractToken(request, TokenType.ACCESS.getValue());
        String username = null;
        String jwtToken = null;
        try {
            jwtToken = token.orElseThrow(() -> new ApiException(ExceptionMessageCode.TOKEN_IS_NOT_VALID));
            username = jwtService.getTokenData(jwtToken, TokenData::getUser).getEmail();
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to get JWT Token");
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token has expired");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User userByEmail = this.userServiceImpl.getUserByEmail(username);
            CredentialEntity credentialEntity = this.userServiceImpl.getUserCredentialById(userByEmail.getId());
            UserDetails userDetails = new UserPrinciple(userByEmail, credentialEntity);

            if (jwtService.isTokenValid(jwtToken)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
