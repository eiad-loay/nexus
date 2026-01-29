package com.novus.ecommerce.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = parseToken(request);
            if (token != null) {
                log.debug("JWT token detected on request to {} {}", request.getMethod(), request.getRequestURI());
                jwtUtils.validateToken(token);

                String username = jwtUtils.extractUsername(token);
                Set<SimpleGrantedAuthority> authorities = jwtUtils.extractRole(token);
                log.debug("JWT validated for username={} with authorities={}", username, authorities);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            log.warn("JWT processing failed: {}", e.getMessage());
            request.setAttribute("jwt_error", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "Access-Token");
        if (cookie != null) {
            String token = cookie.getValue();
            if (token != null) {
                return token;
            }
        }
        return null;
    }
}
