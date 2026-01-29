package com.novus.ecommerce.security.service;

import com.novus.ecommerce.entity.User;
import com.novus.ecommerce.repository.UserRepository;
import com.novus.ecommerce.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "user_details", key = "#username")
    public @NullMarked UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Loading user by username(email)={}", username);
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username")
        );

        log.debug("User loaded with id={} and email={}", user.getId(), user.getEmail());
        return new UserDetailsImpl(user);
    }
}
