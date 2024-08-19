package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.domain.Token;
import com.snap.wallet.demo.wallet_demo.domain.TokenData;
import com.snap.wallet.demo.wallet_demo.dto.User;
import com.snap.wallet.demo.wallet_demo.enumeration.TokenType;
import com.snap.wallet.demo.wallet_demo.function.TriConsumer;
import com.snap.wallet.demo.wallet_demo.security.JwtConfiguration;
import com.snap.wallet.demo.wallet_demo.service.JwtService;
import com.snap.wallet.demo.wallet_demo.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.snap.wallet.demo.wallet_demo.constant.Constant.*;
import static com.snap.wallet.demo.wallet_demo.enumeration.TokenType.ACCESS;
import static java.time.Instant.now;
import static java.util.Arrays.stream;
import static java.util.Date.from;
import static java.util.Optional.empty;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl extends JwtConfiguration implements JwtService {

    private final UserService userService;
    private final Supplier<SecretKey> key = () -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSecret()));

    private final Function<String, Claims> claimsFunction = token -> Jwts.parser().verifyWith(key.get()).build().parseSignedClaims(token).getPayload();

    private final Function<String, String> subject = token -> getClaimValue(token, Claims::getSubject);

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken =
            (request, cookieName) -> Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} :
                    request.getCookies()).filter(cookie -> Objects.equals(cookieName, cookie.getName())).map(Cookie::getValue).findAny()).orElse(empty());

    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) -> Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)}
            : request.getCookies()).filter(cookie -> Objects.equals(cookieName, cookie.getName())).findAny()).orElse(empty());

    private final Supplier<JwtBuilder> builder = () -> Jwts.builder().header().add(Map.of(TYPE, JWT_TYPE)).and().audience().add(GET_ARRAYS_LLC).and().id(UUID.randomUUID().toString()).issuedAt(from(now())).notBefore(new Date()).signWith(key.get(), Jwts.SIG.HS512);

    private final BiFunction<User, TokenType, String> buildToken = (user, type) -> Objects.equals(type, ACCESS) ? builder.get().subject(user.getUserId()).claim(ROLE, user.getRole()).expiration(from(now().plusSeconds(getExpiration()))).compact() : builder.get().subject(user.getUserId()).expiration(from(now().plusSeconds(getExpiration()))).compact();

    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = (response, user, type) -> {
        if (type == ACCESS) {
            String accessToken = createToken(user, Token::getAccess);
            Cookie cookie = new Cookie(type.getValue(), accessToken);
            cookie.setHttpOnly(true);
            //cookie.setSecure(true);
            cookie.setMaxAge(2 * 60 * 60);
            cookie.setPath("/");
            cookie.setAttribute("SameSite", org.springframework.boot.web.server.Cookie.SameSite.STRICT.name());
            response.addCookie(cookie);
        }
    };

    private <T> T getClaimValue(String token, Function<Claims, T> claims) {
        return claimsFunction.andThen(claims).apply(token);
    }


    public Function<String, List<GrantedAuthority>> authorities = token -> commaSeparatedStringToAuthorityList(new StringJoiner(AUTHORITY_DELIMITER).add(claimsFunction.apply(token).get(AUTHORITIES, String.class)).add(ROLE_PREFIX + claimsFunction.apply(token).get(ROLE, String.class)).toString());

    @Override
    public String createToken(User user, Function<Token, String> tokenFunction) {
        Token build = Token.builder().access(buildToken.apply(user, ACCESS)).build();
        return tokenFunction.apply(build);
    }


    @Override
    public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
        return extractToken.apply(request, cookieName);
    }

    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType type) {
        addCookie.accept(response, user, type);
    }

    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(TokenData.builder().valid(Objects.equals(userService.getUserByUserId(subject.apply(token)).getUserId(), claimsFunction.apply(token).getSubject())).authorities(authorities.apply(token)).claims(claimsFunction.apply(token)).user(userService.getUserByUserId(subject.apply(token))).build());
    }

    @Override
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Optional<Cookie> apply = extractCookie.apply(request, cookieName);
        if (apply.isPresent()) {
            Cookie cookie = apply.get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Claims claims = claimsFunction.apply(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
