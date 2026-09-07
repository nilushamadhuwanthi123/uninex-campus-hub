package com.niluverse.uninex.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Runs once Google has confirmed who the visitor is: issues that user an API
 * token and hands it to the frontend by redirecting to it with the token in
 * the URL <em>fragment</em>.
 *
 * The fragment is the reason this is a redirect rather than a JSON response:
 * a browser never sends the part after "#" to any server, and it stays out
 * of the Referer header and out of server access logs, so the token reaches
 * the page without being written down along the way. The page reads it,
 * stores it, and strips it from the address bar immediately.
 */
public class TokenIssuingLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ApiTokenService apiTokenService;
    private final UserRepository userRepository;
    private final String frontendLoginRedirectUrl;

    public TokenIssuingLoginSuccessHandler(ApiTokenService apiTokenService,
        UserRepository userRepository, String frontendLoginRedirectUrl) {
        this.apiTokenService = apiTokenService;
        this.userRepository = userRepository;
        this.frontendLoginRedirectUrl = frontendLoginRedirectUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        String target = frontendLoginRedirectUrl;

        if (authentication.getPrincipal() instanceof OAuth2User principal) {
            String googleId = String.valueOf(principal.getAttribute("sub"));
            // The user row was created by CustomOAuth2UserService moments
            // ago, so a miss here means something is genuinely wrong --
            // send the browser back signed out rather than to an error page,
            // and let the frontend show its normal signed-out state.
            var user = userRepository.findByGoogleId(googleId);
            if (user.isPresent()) {
                String token = apiTokenService.issueFor(user.get());
                target = frontendLoginRedirectUrl + "#token="
                    + URLEncoder.encode(token, StandardCharsets.UTF_8);
            }
        }

        response.sendRedirect(target);
    }
}
