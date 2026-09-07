package com.niluverse.uninex.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates a request carrying "Authorization: Bearer &lt;token&gt;",
 * the scheme the deployed frontend uses because it cannot rely on a
 * cross-site session cookie (see ApiTokenService for why).
 *
 * The principal built here is deliberately the same shape the Google login
 * produces -- a DefaultOAuth2User with email/name attributes and a single
 * ROLE_* authority -- so every existing authorization rule and
 * {@code @AuthenticationPrincipal OAuth2User} parameter keeps working
 * without caring which of the two ways the caller authenticated.
 *
 * The role comes from the user record read here on this request, never from
 * anything the client sent: the token identifies who you are, and the
 * server decides what that means.
 *
 * Deliberately not a Spring bean: Boot auto-registers every Filter bean
 * into the plain servlet chain as well, which would run this a second
 * time outside the security chain. SecurityConfig constructs it instead.
 */
public class ApiTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final ApiTokenService apiTokenService;

    public ApiTokenAuthenticationFilter(ApiTokenService apiTokenService) {
        this.apiTokenService = apiTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Only act when this request actually carries a bearer token and
        // nothing else has authenticated it yet -- a session-cookie login
        // (which is what local development uses) must not be overwritten.
        if (header != null && header.startsWith(BEARER_PREFIX)
            && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(BEARER_PREFIX.length()).trim();
            apiTokenService.resolve(token).ifPresent(user -> {
                Set<GrantedAuthority> authorities = Set.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                );

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("sub", user.getGoogleId());
                attributes.put("email", user.getEmail());
                attributes.put("name", user.getName());

                OAuth2User principal = new DefaultOAuth2User(authorities, attributes, "sub");
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        filterChain.doFilter(request, response);
    }
}
