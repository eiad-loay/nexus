package com.novus.ecommerce.service;

import com.novus.ecommerce.entity.RefreshToken;
import com.novus.ecommerce.entity.User;
import com.novus.ecommerce.exception.custom.EntityNotFoundException;
import com.novus.ecommerce.repository.TokenRepository;
import com.novus.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${refresh_token_expiration}")
    private Long tokenExpiration;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public String generateToken(User user) {

        Optional<RefreshToken> token = tokenRepository.findByUserId(user.getId());

        if (token.isPresent()) {

            if (token.get().getExpiryDate().after(new Date())) {
                return token.get().getToken();
            }

            tokenRepository.delete(token.get());
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(new Date(System.currentTimeMillis() + tokenExpiration))
                .build();

        tokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public String getUserId(String token) {
        RefreshToken refreshToken = tokenRepository.findByToken(token).orElseThrow(
                () -> new EntityNotFoundException("Token not found")
        );

        return refreshToken.getUser().getEmail();
    }

    public boolean validateToken(String token) {
        Optional<RefreshToken> refreshToken = tokenRepository.findByToken(token);
        return refreshToken.isPresent()
                && refreshToken.get().getExpiryDate().after(new Date())
                && userRepository.existsById(refreshToken.get().getUser().getId());
    }

    @Transactional
    public void revokeUserToken(String token) {
        RefreshToken refreshToken = tokenRepository.findByToken(token).orElseThrow(
                () -> new EntityNotFoundException("Token not found")
        );
        tokenRepository.delete(refreshToken);
    }
}
