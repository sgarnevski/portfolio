package com.portfolio.it.steps;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RebalanceSteps {

    private final ScenarioContext context;

    public RebalanceSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I request a full rebalance")
    public void iRequestAFullRebalance() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios/" + context.getPortfolioId() + "/rebalance");

        context.setLastResponse(response);
    }

    @When("I request a cash rebalance with amount {double}")
    public void iRequestACashRebalanceWithAmount(double amount) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("amount", amount))
                .when()
                .post("/api/portfolios/" + context.getPortfolioId() + "/rebalance/cash");

        context.setLastResponse(response);
    }

    // Assertion steps

    @And("the rebalance response contains portfolioId")
    public void theRebalanceResponseContainsPortfolioId() {
        Number portfolioId = context.getLastResponse().path("portfolioId");
        assertThat(portfolioId, is(notNullValue()));
        assertThat(portfolioId.longValue(), is(context.getPortfolioId()));
    }

    @And("the rebalance response contains allocations")
    public void theRebalanceResponseContainsAllocations() {
        List<?> allocations = context.getLastResponse().jsonPath().getList("allocations");
        assertThat(allocations, is(not(empty())));
    }

    @And("the rebalance response contains calculatedAt")
    public void theRebalanceResponseContainsCalculatedAt() {
        String calculatedAt = context.getLastResponse().path("calculatedAt");
        assertThat(calculatedAt, is(notNullValue()));
    }
}
