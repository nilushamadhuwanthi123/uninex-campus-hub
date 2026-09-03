package com.niluverse.uninex.config;

import com.niluverse.uninex.auth.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Real Google OAuth2 login + role-based authorization (Issue #5), replacing
 * the earlier permitAll scaffold. Rules are ordered most-specific first --
 * Spring Security uses the first matcher that matches a request, so a
 * broad "/api/bookings/**" rule must never come before a narrower
 * "/api/bookings/*&#47;approve" rule or it would shadow it.
 *
 * Role checks here are the only source of truth for who can call an
 * admin-only endpoint -- there is no client-side gate anywhere that this
 * config relies on, per the "never trust frontend-only login state" rule.
 */
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public read access -- anyone can browse resources/bookings/incidents/reviews.
                .requestMatchers(HttpMethod.GET, "/api/resources/**", "/api/bookings/**",
                    "/api/incidents/**", "/api/reviews/**").permitAll()
                .requestMatchers("/api/auth/me").permitAll()

                // Staff/admin-only actions -- must be listed before the broader
                // POST/DELETE rules below so they aren't shadowed.
                .requestMatchers("/api/bookings/*/approve", "/api/bookings/*/reject").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/incidents/*/assign", "/api/incidents/*/start",
                    "/api/incidents/*/resolve", "/api/incidents/*/close").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/resources/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/resources/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/resources/**").hasAnyRole("STAFF", "ADMIN")
                // Removing someone else's review is moderation, not self-service.
                .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasAnyRole("STAFF", "ADMIN")

                // Any logged-in user can request a booking, report an incident,
                // or leave a review.
                .requestMatchers(HttpMethod.POST, "/api/bookings/**", "/api/incidents/**", "/api/reviews/**")
                    .authenticated()

                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            );
        return http.build();
    }
}
