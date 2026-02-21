package com.portfolio.it.hooks;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.java.After;

import static io.restassured.RestAssured.given;

public class CleanupHooks {

    private final ScenarioContext context;

    public CleanupHooks(ScenarioContext context) {
        this.context = context;
    }

    @After
    public void cleanup() {
        if (context.getAuthToken() == null) {
            return;
        }

        for (Long portfolioId : context.getCreatedPortfolioIds()) {
            try {
                given()
                        .baseUri(context.getBackendBaseUrl())
                        .header("Authorization", "Bearer " + context.getAuthToken())
                        .when()
                        .delete("/api/portfolios/" + portfolioId);
            } catch (Exception ignored) {
            }
        }

        for (Long currencyId : context.getCreatedCurrencyIds()) {
            try {
                given()
                        .baseUri(context.getBackendBaseUrl())
                        .header("Authorization", "Bearer " + context.getAuthToken())
                        .when()
                        .delete("/api/currencies/" + currencyId);
            } catch (Exception ignored) {
            }
        }
    }
}
