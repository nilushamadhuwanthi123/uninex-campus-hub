package com.niluverse.uninex.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApiTokenServiceTest {

    @Mock
    private UserRepository userRepository;

    private ApiTokenService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new ApiTokenService(userRepository);
        user = new User("google-123", "student@example.com", "Test Student");
    }

    @Test
    void issueFor_storesOnlyTheHash_neverTheTokenItself() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String token = service.issueFor(user);

        assertThat(token).isNotBlank();
        assertThat(user.getApiTokenHash())
            .isNotNull()
            .isNotEqualTo(token)
            .isEqualTo(ApiTokenService.hash(token));
    }

    @Test
    void issueFor_setsAnExpiryInTheFuture() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        service.issueFor(user);

        assertThat(user.getApiTokenExpiresAt()).isAfter(Instant.now());
    }

    @Test
    void issueFor_givesADifferentTokenEveryTime() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String first = service.issueFor(user);
        String second = service.issueFor(user);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void resolve_findsTheUser_forAValidToken() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        String token = service.issueFor(user);
        when(userRepository.findByApiTokenHash(ApiTokenService.hash(token)))
            .thenReturn(Optional.of(user));

        assertThat(service.resolve(token)).contains(user);
    }

    @Test
    void resolve_rejectsAnExpiredToken() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        String token = service.issueFor(user);
        user.setApiTokenExpiresAt(Instant.now().minusSeconds(1));
        when(userRepository.findByApiTokenHash(ApiTokenService.hash(token)))
            .thenReturn(Optional.of(user));

        assertThat(service.resolve(token)).isEmpty();
    }

    @Test
    void resolve_rejectsAnUnknownOrMissingToken() {
        when(userRepository.findByApiTokenHash(any())).thenReturn(Optional.empty());

        assertThat(service.resolve("not-a-real-token")).isEmpty();
        assertThat(service.resolve(null)).isEmpty();
        assertThat(service.resolve("  ")).isEmpty();
    }

    @Test
    void revokeFor_clearsTheStoredToken() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        service.issueFor(user);

        service.revokeFor(user);

        assertThat(user.getApiTokenHash()).isNull();
        assertThat(user.getApiTokenExpiresAt()).isNull();
    }
}
