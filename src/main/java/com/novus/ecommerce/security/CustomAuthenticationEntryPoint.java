package com.novus.ecommerce.security;

import com.novus.ecommerce.dto.response.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            response.setHeader("WWW-Authenticate",
                    "Bearer error=\"invalid_token\"");
        } else {
            response.setHeader("WWW-Authenticate", "Bearer realm=\"Authentication System\"");
        }

        List<String> errorList = new ArrayList<>();

        if (request.getAttribute("jwt_error") != null)
            errorList.add((String) request.getAttribute("jwt_error"));
        else
            errorList.add(authException.getMessage());

        Response<?> body = Response.builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .message("Authorization required")
                .errorList(errorList)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writer().writeValue(response.getOutputStream(), body);
    }
}
