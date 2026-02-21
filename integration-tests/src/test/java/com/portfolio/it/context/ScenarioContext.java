package com.portfolio.it.context;

import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

public class ScenarioContext {

    private static final String BACKEND_BASE_URL = "http://localhost:8080";
    private static final String AUTH_BASE_URL = "http://localhost:8090";

    private String authToken;
    private Long portfolioId;
    private Long holdingId;
    private Long tradeId;
    private Long currencyId;
    private Response lastResponse;

    private String originalUsername;
    private String originalEmail;
    private String originalPassword;

    private final List<Long> createdPortfolioIds = new ArrayList<>();
    private final List<Long> createdCurrencyIds = new ArrayList<>();

    public String getBackendBaseUrl() {
        return BACKEND_BASE_URL;
    }

    public String getAuthBaseUrl() {
        return AUTH_BASE_URL;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(Long holdingId) {
        this.holdingId = holdingId;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }

    public String getOriginalUsername() {
        return originalUsername;
    }

    public void setOriginalUsername(String originalUsername) {
        this.originalUsername = originalUsername;
    }

    public String getOriginalEmail() {
        return originalEmail;
    }

    public void setOriginalEmail(String originalEmail) {
        this.originalEmail = originalEmail;
    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    public List<Long> getCreatedPortfolioIds() {
        return createdPortfolioIds;
    }

    public void addCreatedPortfolioId(Long id) {
        createdPortfolioIds.add(id);
    }

    public List<Long> getCreatedCurrencyIds() {
        return createdCurrencyIds;
    }

    public void addCreatedCurrencyId(Long id) {
        createdCurrencyIds.add(id);
    }
}
