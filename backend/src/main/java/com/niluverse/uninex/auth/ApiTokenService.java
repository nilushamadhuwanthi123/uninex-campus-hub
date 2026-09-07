package com.niluverse.uninex.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Issues and verifies the bearer tokens the frontend authenticates with.
 *
 * Why this exists at all: the frontend is served from GitHub Pages and this
 * API from Render, so they are different sites. A session cookie set by the
 * API is a third-party cookie to the page, and browsers now block those by
 * default -- so login completed but every subsequent request arrived
 * anonymous. Marking the cookie SameSite=None did not help, because the
 * block is not about SameSite. A bearer token the page sends explicitly is
 * not a cookie, so no cookie policy applies to it.
 *
 * The token is a 256-bit random value. Only its SHA-256 is stored, so
 * someone who reads the users collection cannot replay the tokens in it --
 * the same reason password hashes are stored rather than passwords. There
 * is no secret key to manage and nothing to rotate, which is the advantage
 * of an opaque token over a signed one at this size of project; the cost is
 * that verifying one requires a database lookup.
 */
@Service
public class ApiTokenService {

    /**
     * How long a token stays valid. Long enough not to interrupt normal use,
     * short enough that a token copied out of a browser does not work
     * indefinitely.
     */
    static final Duration TOKEN_LIFETIME = Duration.ofDays(7);

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;

    public ApiTokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Issues a fresh token for the user and stores its hash, replacing any
     * token they held before -- so signing in again invalidates the old one
     * rather than leaving a second working credential behind.
     *
     * @return the raw token, which is returned to the browser this once and
     *     never recoverable from the database afterwards.
     */
    public String issueFor(User user) {
        byte[] raw = new byte[32];
        RANDOM.nextBytes(raw);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);

        user.setApiTokenHash(hash(token));
        user.setApiTokenExpiresAt(Instant.now().plus(TOKEN_LIFETIME));
        userRepository.save(user);

        return token;
    }

    /**
     * Resolves a raw bearer token back to its user, or empty if the token is
     * unknown or has expired. An expired token is treated exactly like an
     * unknown one so a caller cannot tell the difference.
     */
    public Optional<User> resolve(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByApiTokenHash(hash(token))
            .filter(user -> {
                Instant expiry = user.getApiTokenExpiresAt();
                return expiry != null && expiry.isAfter(Instant.now());
            });
    }

    /**
     * Drops the user's current token, so signing out on one machine cannot
     * be undone by keeping the old value around.
     */
    public void revokeFor(User user) {
        user.setApiTokenHash(null);
        user.setApiTokenExpiresAt(null);
        userRepository.save(user);
    }

    static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is required of every Java platform, so this cannot
            // happen -- but the checked exception still has to go somewhere.
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
