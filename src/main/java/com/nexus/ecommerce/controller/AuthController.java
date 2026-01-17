package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.request.LoginRequest;
import com.nexus.ecommerce.dto.request.RegisterRequest;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.service.AuthService;
import com.nexus.ecommerce.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        log.info("HTTP POST /api/auth/login for email={}", request.getEmail());
        List<ResponseCookie> cookies = authService.login(request);

        cookies.forEach(cookie -> {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        });

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .build();
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> register(@RequestBody @Valid RegisterRequest request) {
        log.info("HTTP POST /api/auth/register");
        return authService.register(request);
    }

    @GetMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> verifyToken(@RequestParam String token) {
        log.info("HTTP GET /api/auth/verify");

        return emailVerificationService.handleVerification(token);
    }

    @PostMapping(value = "/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws BadRequestException {
        log.info("HTTP POST /api/auth/refresh-token");
        ResponseCookie token = authService.refreshToken(request);

        response.addHeader(HttpHeaders.SET_COOKIE, token.toString());

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .build();
    }
}
