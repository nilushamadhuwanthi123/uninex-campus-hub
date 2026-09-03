package com.niluverse.uninex.auth;

import java.util.Map;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Turns a Google login into a local {@link User} record and a Spring
 * Security principal carrying that user's role as a ROLE_* authority.
 *
 * Security note (from the "never trust frontend-only login state" rule):
 * the role used for every authorization check below comes from this
 * server-side lookup on every request that needs a fresh principal --
 * never from anything the client sends.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User googleUser = super.loadUser(userRequest);
        return buildAuthenticatedUser(googleUser.getAttributes());
    }

    /**
     * Finds or creates the local user for a Google identity and wraps it as
     * an OAuth2User with a single ROLE_&lt;role&gt; authority. Split out from
     * loadUser() so the find-or-create + role-mapping logic can be unit
     * tested without a live call to Google's userinfo endpoint.
     */
    public OAuth2User buildAuthenticatedUser(Map<String, Object> attributes) {
        String googleId = String.valueOf(attributes.get("sub"));
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        User user = userRepository.findByGoogleId(googleId)
            .orElseGet(() -> userRepository.save(new User(googleId, email, name)));

        Set<GrantedAuthority> authorities = Set.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new DefaultOAuth2User(authorities, attributes, "sub");
    }
}
