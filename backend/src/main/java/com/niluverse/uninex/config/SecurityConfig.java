package com.niluverse.uninex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Scaffold-stage security config: permits all requests so the REST API can be
 * built and tested feature-by-feature. Real auth (Google OAuth2 login + role
 * checks per the planned feature list) lands in its own branch/issue before
 * this project is treated as done -- this is a deliberate placeholder, not
 * the final state.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
