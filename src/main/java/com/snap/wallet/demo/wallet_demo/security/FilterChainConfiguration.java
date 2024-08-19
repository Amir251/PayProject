package com.snap.wallet.demo.wallet_demo.security;

import com.snap.wallet.demo.wallet_demo.service.JwtService;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class FilterChainConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationFilter authenticationFilter, CustomRequestContextFilter customRequestContextFilter) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customRequestContextFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("api/users/**", "/swagger-ui/**","/v3/**","api/products/findAllProducts").permitAll()
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        http.logout(logout ->
                logout
                        .logoutUrl("/api/users/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                        .deleteCookies("access-token")
                        .invalidateHttpSession(true)
        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserService userService, BCryptPasswordEncoder encoder) {
        return new ProviderManager(new ApiAuthenticationProvider(userService, encoder));
    }

    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }

    @Bean
    public AuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        return new AuthenticationFilter(authenticationManager, userService, jwtService);
    }

    @Bean
    public CustomRequestContextFilter customRequestContextFilter() {
        return new CustomRequestContextFilter();
    }
}
