package com.novus.ecommerce.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret_key}")
    private String secret;

    @Value("${jwt.access_token_expiration}")
    private long accessTokenExpiration;

    public String generateToken(UserDetails userDetails) {
        String role = null;
        if (userDetails.getAuthorities().stream().findFirst().isPresent()) {
            role = userDetails.getAuthorities()
                    .stream()
                    .findFirst()
                    .get()
                    .getAuthority();
        }

        log.debug("Generating JWT token for username={} with role={}", userDetails.getUsername(), role);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(Duration.ofMillis(accessTokenExpiration))))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateVerificationToken(String email) {
        log.debug("Generating email verification token for email={}", email);
        return Jwts.builder()
                .subject(email)
                .claim("purpose", "email_verification")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(Duration.ofMillis(accessTokenExpiration))))
                .signWith(getSecretKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Set<SimpleGrantedAuthority> extractRole(String token) {
        Object res = extractAllClaims(token).get("role");

        return Collections.singleton(new SimpleGrantedAuthority((String) res));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public void validateToken(String token) {
        log.trace("Validating JWT token");
        Jwts
                .parser()
                .verifyWith((SecretKey) getSecretKey())
                .build()
                .parse(token);
    }

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
