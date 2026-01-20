package com.nexus.ecommerce.service;

import com.nexus.ecommerce.dto.request.LoginRequest;
import com.nexus.ecommerce.dto.request.RegisterRequest;
import com.nexus.ecommerce.dto.response.RegisterResponse;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.entity.Cart;
import com.nexus.ecommerce.entity.ERole;
import com.nexus.ecommerce.entity.Role;
import com.nexus.ecommerce.entity.User;
import com.nexus.ecommerce.exception.custom.EntityNotFoundException;
import com.nexus.ecommerce.exception.custom.UserAlreadyExistException;
import com.nexus.ecommerce.exception.custom.UserNotEnabledException;
import com.nexus.ecommerce.repository.CartRepository;
import com.nexus.ecommerce.repository.RoleRepository;
import com.nexus.ecommerce.repository.UserRepository;
import com.nexus.ecommerce.security.jwt.JwtUtils;
import com.nexus.ecommerce.utils.event.UserRegistrationEvent;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${jwt.access_token_expiration}")
    private Long accessTokenExpiration;

    @Value("${refresh_token_expiration}")
    private Long refreshTokenExpiration;

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final CartRepository cartRepository;

    public List<ResponseCookie> login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        log.info("Login attempt for email={}", email);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found with email: " + email)
        );

        if (!user.isEnabled()) {
            throw new UserNotEnabledException("Please verify your email before login");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
        );

        log.debug("Authentication successful for email={}", email);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateToken((UserDetails) Objects.requireNonNull(authentication.getPrincipal()));
        String refreshToken = tokenService.generateToken(user);

        ResponseCookie access = ResponseCookie.from("Access-Token", accessToken)
                .httpOnly(true)
                .secure(false)
                .maxAge(accessTokenExpiration / 1000)
                .path("/")
                .build();

        ResponseCookie refresh = ResponseCookie.from("Refresh-Token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .maxAge(refreshTokenExpiration / 1000)
                .path("/")
                .build();

        log.info("Login successful for email={}", email);

        return List.of(access, refresh);
    }

    @Transactional
    public Response<RegisterResponse> register(RegisterRequest registerRequest) {

        log.info("Registration attempt for email={}", registerRequest.getEmail());

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: email already exists email={}", registerRequest.getEmail());
            throw new UserAlreadyExistException("This email already exists");
        }

        Role role = roleRepository.findByRoleName(ERole.ROLE_CUSTOMER).orElseThrow(
                () -> new EntityNotFoundException("Role not found")
        );

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(role)
                .build();

        Cart cart = Cart.builder()
                .user(user)
                .items(new HashSet<>())
                .build();

        cartRepository.save(cart);

        userRepository.save(user);
        log.info("User persisted with email={} and role={}", user.getEmail(), role.getRoleName());
        eventPublisher.publishEvent(new UserRegistrationEvent(registerRequest.getEmail()));
        log.info("UserRegistrationEvent published for email={}", registerRequest.getEmail());

        RegisterResponse response = RegisterResponse.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .message("A verification Email has been sent to your email")
                .build();

        return Response.<RegisterResponse>builder()
                .status(HttpStatus.CREATED.value())
                .data(response)
                .message("Register Successful")
                .build();
    }

    public ResponseCookie refreshToken(HttpServletRequest request) throws BadRequestException {
        Cookie refreshTokenCookie = WebUtils.getCookie(request, "Refresh-Token");
        String refreshTokenValue;

        if (refreshTokenCookie != null) {
            refreshTokenValue = refreshTokenCookie.getValue();
        } else {
            throw new BadRequestException("Refresh-Token is required");
        }

        if (tokenService.validateToken(refreshTokenValue)) {
            String email = tokenService.getUserId(refreshTokenValue);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            String accessToken = jwtUtils.generateToken(userDetails);

            return ResponseCookie.from("Access-Token", accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .maxAge(accessTokenExpiration / 1000)
                    .path("/")
                    .build();
        }

        throw new IllegalArgumentException("Invalid token");
    }

}