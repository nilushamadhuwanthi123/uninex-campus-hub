package com.niluverse.uninex.auth;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the currently logged-in user's own identity. Every response here
 * carries Cache-Control: no-store so a shared/browser-back cache never
 * serves one person's private session data to the next visitor on a shared
 * machine -- the same rule flagged for any endpoint returning per-user data.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401)
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache")
                .build();
        }

        Map<String, Object> body = Map.of(
            "email", String.valueOf(principal.getAttribute("email")),
            "name", String.valueOf(principal.getAttribute("name")),
            "roles", principal.getAuthorities()
        );

        return ResponseEntity.ok()
            .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache")
            .body(body);
    }
}
