package com.novus.ecommerce.service;

import com.novus.ecommerce.dto.response.Response;
import com.novus.ecommerce.entity.User;
import com.novus.ecommerce.exception.custom.EntityNotFoundException;
import com.novus.ecommerce.repository.UserRepository;
import com.novus.ecommerce.security.jwt.JwtUtils;
import com.novus.ecommerce.utils.event.UserRegistrationEvent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    @Value("${app.base-url}")
    private String baseUrl;

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;
    private final MailService mailService;

    @EventListener
    @Async
    public void handleUserRegistrationEvent(UserRegistrationEvent event) {
        try {
            String token = jwtUtils.generateVerificationToken(event.email());
            Resource resource = resourceLoader.getResource("classpath:templates/verification-email.html");

            String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/api/auth/verify")
                    .queryParam("token", token)
                    .build()
                    .toUri();

            template = template.replace("{{verificationUrl}}", uri.toString());

            mailService.sendHtmlMail(event.email(), "Verification Email", template);

            log.info("Email sent to {}", event.email());
        } catch (IOException | MessagingException e) {
            log.error("Error while sending verification email", e);
        }
    }

    public Response<?> handleVerification(String token) {

        log.info("Handling email verification");
        jwtUtils.validateToken(token);

        Claims claims = jwtUtils.extractAllClaims(token);

        if (!"email_verification".equals(claims.get("purpose"))) {
            log.warn("Received token with invalid purpose={}", claims.get("purpose"));
            throw new JwtException("Invalid token");
        }

        String email = claims.getSubject();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        if (user.isEnabled()) {
            log.info("Email already verified for user with email={}", email);
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Email already verified")
                    .build();
        }

        user.setEnabled(true);
        userRepository.save(user);
        log.info("Email verified successfully for user with email={}", email);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Email verified")
                .build();
    }
}
