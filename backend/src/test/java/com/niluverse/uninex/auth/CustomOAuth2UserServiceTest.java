package com.niluverse.uninex.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void buildAuthenticatedUser_createsNewUser_whenFirstLogin() {
        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository);
        Map<String, Object> attributes = Map.of(
            "sub", "google-123",
            "email", "student@example.com",
            "name", "Test Student"
        );

        when(userRepository.findByGoogleId("google-123")).thenReturn(Optional.empty());
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        OAuth2User result = service.buildAuthenticatedUser(attributes);

        assertThat(result.getAttribute("email").toString()).isEqualTo("student@example.com");
        assertThat(result.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_STUDENT");
    }

    @Test
    void buildAuthenticatedUser_reusesExistingUser_andKeepsAssignedRole() {
        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository);
        Map<String, Object> attributes = Map.of(
            "sub", "google-456",
            "email", "admin@example.com",
            "name", "Test Admin"
        );

        User existing = new User("google-456", "admin@example.com", "Test Admin");
        existing.setId("u1");
        existing.setRole(Role.ADMIN);
        when(userRepository.findByGoogleId("google-456")).thenReturn(Optional.of(existing));

        OAuth2User result = service.buildAuthenticatedUser(attributes);

        assertThat(result.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_ADMIN");
    }

    @Test
    void buildAuthenticatedUser_defaultsNewUsers_toStudentRole() {
        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository);
        Map<String, Object> attributes = Map.of(
            "sub", "google-789",
            "email", "new@example.com",
            "name", "New Person"
        );

        when(userRepository.findByGoogleId("google-789")).thenReturn(Optional.empty());
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
            .thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                assertThat(u.getRole()).isEqualTo(Role.STUDENT);
                return u;
            });

        service.buildAuthenticatedUser(attributes);
    }
}
