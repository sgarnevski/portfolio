package com.portfolio.rebalancer.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

@Component
public class ServiceTokenManager {

    private static final Logger log = LoggerFactory.getLogger(ServiceTokenManager.class);

    private final RestClient tokenClient;
    private final String clientId;
    private final String clientSecret;

    private String cachedToken;
    private Instant expiresAt = Instant.MIN;

    public ServiceTokenManager(
            RestClient.Builder restClientBuilder,
            @Value("${app.auth.token-url}") String tokenUrl,
            @Value("${app.auth.client-id}") String clientId,
            @Value("${app.auth.client-secret}") String clientSecret) {
        this.tokenClient = restClientBuilder.baseUrl(tokenUrl).build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public synchronized String getToken() {
        if (cachedToken != null && Instant.now().isBefore(expiresAt)) {
            return cachedToken;
        }
        return refreshToken();
    }

    private String refreshToken() {
        log.debug("Requesting new service token from auth-service");
        try {
            TokenResponse response = tokenClient.post()
                    .uri("")
                    .header("Content-Type", "application/json")
                    .body(Map.of(
                            "clientId", clientId,
                            "clientSecret", clientSecret,
                            "grantType", "client_credentials"
                    ))
                    .retrieve()
                    .body(TokenResponse.class);

            cachedToken = response.accessToken;
            expiresAt = Instant.now().plusSeconds(response.expiresIn - 60);
            log.debug("Service token acquired, expires at {}", expiresAt);
            return cachedToken;
        } catch (Exception e) {
            log.error("Failed to obtain service token: {}", e.getMessage());
            throw new RuntimeException("Unable to obtain service token from auth-service", e);
        }
    }

    private static class TokenResponse {
        @JsonProperty("access_token")
        String accessToken;

        @JsonProperty("token_type")
        String tokenType;

        @JsonProperty("expires_in")
        long expiresIn;

        String scope;
    }
}
