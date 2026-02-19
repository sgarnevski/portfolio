package com.portfolio.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.portfolio.auth.dto.ChangePasswordRequest;
import com.portfolio.auth.dto.GoogleTokenRequest;
import com.portfolio.auth.dto.LoginRequest;
import com.portfolio.auth.dto.RegisterRequest;
import com.portfolio.auth.dto.UpdateProfileRequest;
import com.portfolio.auth.dto.AuthResponse;
import com.portfolio.auth.dto.UserProfileResponse;
import com.portfolio.auth.entity.AuthProvider;
import com.portfolio.auth.entity.User;
import com.portfolio.auth.repository.UserRepository;
import com.portfolio.auth.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager,
                       GoogleTokenVerifier googleTokenVerifier) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.googleTokenVerifier = googleTokenVerifier;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }

    @Transactional
    public AuthResponse exchangeGoogleToken(GoogleTokenRequest request) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(request.getIdToken());
        if (payload == null) {
            throw new IllegalArgumentException("Invalid Google ID token");
        }

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .map(existingUser -> linkGoogleAccount(existingUser, googleId))
                        .orElseGet(() -> createGoogleUser(googleId, email, name)));

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }

    public UserProfileResponse getProfile() {
        User user = getCurrentUser();
        return toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        if (user.getPasswordHash() == null) {
            throw new IllegalArgumentException("Cannot change password for OAuth-only account");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
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

    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}
