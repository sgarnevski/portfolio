package com.portfolio.auth.controller;

import com.portfolio.auth.dto.AuthResponse;
import com.portfolio.auth.dto.ChangePasswordRequest;
import com.portfolio.auth.dto.ClientCredentialsRequest;
import com.portfolio.auth.dto.GoogleTokenRequest;
import com.portfolio.auth.dto.LoginRequest;
import com.portfolio.auth.dto.RegisterRequest;
import com.portfolio.auth.dto.ServiceTokenResponse;
import com.portfolio.auth.dto.UpdateProfileRequest;
import com.portfolio.auth.dto.UserProfileResponse;
import com.portfolio.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register, login, Google token exchange, and user profile")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/token")
    @Operation(summary = "Issue service token via client_credentials grant")
    @SecurityRequirements
    public ResponseEntity<ServiceTokenResponse> issueToken(@Valid @RequestBody ClientCredentialsRequest request) {
        return ResponseEntity.ok(authService.issueServiceToken(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @SecurityRequirements
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    @SecurityRequirements
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/google")
    @Operation(summary = "Exchange Google ID token for JWT")
    @SecurityRequirements
    public ResponseEntity<AuthResponse> googleExchange(@Valid @RequestBody GoogleTokenRequest request) {
        return ResponseEntity.ok(authService.exchangeGoogleToken(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(authService.getProfile());
    }

    @PutMapping("/me")
    @Operation(summary = "Update username and email")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(request));
    }

    @PostMapping("/me/change-password")
    @Operation(summary = "Change password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}
