package com.nexus.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Allow frontend origin
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Allow all standard HTTP methods
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow all headers
        corsConfiguration.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers)
        corsConfiguration.setAllowCredentials(true);

        // Expose headers that frontend can read
        corsConfiguration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

        // Cache preflight requests for 1 hour
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
