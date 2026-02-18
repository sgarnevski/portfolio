package com.portfolio.rebalancer.security;

import com.portfolio.rebalancer.entity.AuthProvider;
import com.portfolio.rebalancer.entity.User;
import com.portfolio.rebalancer.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final String frontendRedirectUrl;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository,
                                              JwtTokenProvider jwtTokenProvider,
                                              @Value("${app.oauth2.frontend-redirect-url}") String frontendRedirectUrl) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.frontendRedirectUrl = frontendRedirectUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String googleId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .map(existingUser -> linkGoogleAccount(existingUser, googleId))
                        .orElseGet(() -> createGoogleUser(googleId, email, name)));

        String token = jwtTokenProvider.generateToken(user.getUsername());

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendRedirectUrl)
                .queryParam("token", URLEncoder.encode(token, StandardCharsets.UTF_8))
                .queryParam("username", URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8))
                .queryParam("userId", user.getId())
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }

    private User linkGoogleAccount(User existingUser, String googleId) {
        logger.info("Linking Google account to existing user: {}", existingUser.getUsername());
        existingUser.setGoogleId(googleId);
        return userRepository.save(existingUser);
    }

    private User createGoogleUser(String googleId, String email, String name) {
        String baseUsername = deriveUsername(name, email);
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix;
            suffix++;
        }

        logger.info("Creating new Google user: {}", username);
        User user = User.builder()
                .username(username)
                .email(email)
                .authProvider(AuthProvider.GOOGLE)
                .googleId(googleId)
                .build();
        return userRepository.save(user);
    }

    private String deriveUsername(String name, String email) {
        if (name != null && !name.isBlank()) {
            return name.replaceAll("\\s+", "").toLowerCase();
        }
        return email.substring(0, email.indexOf('@')).toLowerCase();
    }
}
