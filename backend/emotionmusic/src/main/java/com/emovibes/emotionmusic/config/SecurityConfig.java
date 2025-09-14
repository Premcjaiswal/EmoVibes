package com.emovibes.emotionmusic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)                 // Disable CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource))  // Enable CORS
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());  // Allow all requests without auth
        return http.build();
    }
}
