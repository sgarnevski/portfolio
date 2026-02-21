package com.portfolio.it.steps;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PortfolioSteps {

    private final ScenarioContext context;
    private Long deletedPortfolioId;

    public PortfolioSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I create a portfolio with name {string}")
    public void iCreateAPortfolioWithName(String name) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("name", name))
                .when()
                .post("/api/portfolios");

        context.setLastResponse(response);

        if (response.statusCode() == 201) {
            Long id = response.jsonPath().getLong("id");
            context.setPortfolioId(id);
            context.addCreatedPortfolioId(id);
        }
    }

    @When("I create a portfolio with name {string} description {string} driftThreshold {double} and cashBalance {double}")
    public void iCreateAPortfolioWithAllParams(String name, String description, double driftThreshold, double cashBalance) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description);
        body.put("driftThreshold", driftThreshold);
        body.put("cashBalance", cashBalance);

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/portfolios");

        context.setLastResponse(response);

        if (response.statusCode() == 201) {
            Long id = response.jsonPath().getLong("id");
            context.setPortfolioId(id);
            context.addCreatedPortfolioId(id);
        }
    }

    @Given("I have a portfolio named {string}")
    public void iHaveAPortfolioNamed(String name) {
        iCreateAPortfolioWithName(name);
        assertThat(context.getLastResponse().statusCode(), is(201));
    }

    @Given("I have a portfolio named {string} with cashBalance {double}")
    public void iHaveAPortfolioNamedWithCashBalance(String name, double cashBalance) {
        iCreateAPortfolioWithAllParams(name, "", 5.0, cashBalance);
        assertThat(context.getLastResponse().statusCode(), is(201));
    }

    @When("I list all portfolios")
    public void iListAllPortfolios() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios");

        context.setLastResponse(response);
    }

    @When("I get the portfolio by ID")
    public void iGetThePortfolioById() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios/" + context.getPortfolioId());

        context.setLastResponse(response);
    }

    @When("I get portfolio with ID {long}")
    public void iGetPortfolioWithId(long id) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios/" + id);

        context.setLastResponse(response);
    }

    @When("I update the portfolio name to {string}")
    public void iUpdateThePortfolioNameTo(String name) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("name", name))
                .when()
                .put("/api/portfolios/" + context.getPortfolioId());

        context.setLastResponse(response);
    }

    @When("I update the portfolio cash balance to {double}")
    public void iUpdateThePortfolioCashBalanceTo(double cashBalance) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("cashBalance", cashBalance))
                .when()
                .patch("/api/portfolios/" + context.getPortfolioId() + "/cash-balance");

        context.setLastResponse(response);
    }

    @When("I create a portfolio with name {string} and baseCurrency {string}")
    public void iCreateAPortfolioWithNameAndBaseCurrency(String name, String baseCurrency) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("baseCurrency", baseCurrency);

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/portfolios");

        context.setLastResponse(response);

        if (response.statusCode() == 201) {
            Long id = response.jsonPath().getLong("id");
            context.setPortfolioId(id);
            context.addCreatedPortfolioId(id);
        }
    }

    @When("I update the portfolio baseCurrency to {string}")
    public void iUpdateThePortfolioBaseCurrencyTo(String baseCurrency) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", context.getLastResponse().path("name"));
        body.put("baseCurrency", baseCurrency);

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/portfolios/" + context.getPortfolioId());

        context.setLastResponse(response);
    }

    @When("I delete the portfolio")
    public void iDeleteThePortfolio() {
        deletedPortfolioId = context.getPortfolioId();

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .delete("/api/portfolios/" + context.getPortfolioId());

        context.setLastResponse(response);
        context.getCreatedPortfolioIds().remove(context.getPortfolioId());
    }

    @When("I get the deleted portfolio by ID")
    public void iGetTheDeletedPortfolioById() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios/" + deletedPortfolioId);

        context.setLastResponse(response);
    }

    // Assertion steps

    @And("the portfolio name is {string}")
    public void thePortfolioNameIs(String name) {
        assertThat(context.getLastResponse().path("name"), is(equalTo(name)));
    }

    @And("the portfolio description is {string}")
    public void thePortfolioDescriptionIs(String description) {
        assertThat(context.getLastResponse().path("description"), is(equalTo(description)));
    }

    @And("the portfolio driftThreshold is {double}")
    public void thePortfolioDriftThresholdIs(double driftThreshold) {
        Number actual = context.getLastResponse().path("driftThreshold");
        assertThat(actual.doubleValue(), is(closeTo(driftThreshold, 0.01)));
    }

    @And("the portfolio cashBalance is {double}")
    public void thePortfolioCashBalanceIs(double cashBalance) {
        Number actual = context.getLastResponse().path("cashBalance");
        assertThat(actual.doubleValue(), is(closeTo(cashBalance, 0.01)));
    }

    @And("the portfolio baseCurrency is {string}")
    public void thePortfolioBaseCurrencyIs(String baseCurrency) {
        assertThat(context.getLastResponse().path("baseCurrency"), is(equalTo(baseCurrency)));
    }

    @And("the portfolio list is not empty")
    public void thePortfolioListIsNotEmpty() {
        List<?> list = context.getLastResponse().jsonPath().getList("$");
        assertThat(list, is(not(empty())));
    }
}
