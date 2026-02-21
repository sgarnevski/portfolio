package com.portfolio.it.steps;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HoldingSteps {

    private final ScenarioContext context;

    public HoldingSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I create a holding with ticker {string} name {string} assetClass {string} and currency {string}")
    public void iCreateAHolding(String ticker, String name, String assetClass, String currency) {
        Map<String, Object> body = new HashMap<>();
        body.put("tickerSymbol", ticker);
        body.put("name", name);
        body.put("assetClass", assetClass);
        body.put("currency", currency);

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/portfolios/" + context.getPortfolioId() + "/holdings");

        context.setLastResponse(response);

        if (response.statusCode() == 201) {
            context.setHoldingId(response.jsonPath().getLong("id"));
        }
    }

    @When("I list holdings for the portfolio")
    public void iListHoldingsForThePortfolio() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios/" + context.getPortfolioId() + "/holdings");

        context.setLastResponse(response);
    }

    @When("I update the holding name to {string}")
    public void iUpdateTheHoldingNameTo(String name) {
        Response current = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios/" + context.getPortfolioId() + "/holdings");

        List<Map<String, Object>> holdings = current.jsonPath().getList("$");
        Map<String, Object> holding = holdings.stream()
                .filter(h -> ((Number) h.get("id")).longValue() == context.getHoldingId())
                .findFirst()
                .orElseThrow();

        Map<String, Object> body = new HashMap<>();
        body.put("tickerSymbol", holding.get("tickerSymbol"));
        body.put("name", name);
        body.put("assetClass", holding.get("assetClass"));
        body.put("currency", holding.get("currency"));

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/portfolios/" + context.getPortfolioId() + "/holdings/" + context.getHoldingId());

        context.setLastResponse(response);
    }

    @When("I delete the holding")
    public void iDeleteTheHolding() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .delete("/api/portfolios/" + context.getPortfolioId() + "/holdings/" + context.getHoldingId());

        context.setLastResponse(response);
    }

    @When("I get the holding details")
    public void iGetTheHoldingDetails() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios/" + context.getPortfolioId() + "/holdings");

        List<Map<String, Object>> holdings = response.jsonPath().getList("$");
        Map<String, Object> holding = holdings.stream()
                .filter(h -> ((Number) h.get("id")).longValue() == context.getHoldingId())
                .findFirst()
                .orElseThrow();

        context.setLastResponse(response);
    }

    // Assertion steps

    @And("the holding ticker is {string}")
    public void theHoldingTickerIs(String ticker) {
        assertThat(context.getLastResponse().path("tickerSymbol"), is(equalTo(ticker)));
    }

    @And("the holding currency is {string}")
    public void theHoldingCurrencyIs(String currency) {
        assertThat(context.getLastResponse().path("currency"), is(equalTo(currency)));
    }

    @And("the holding name is {string}")
    public void theHoldingNameIs(String name) {
        assertThat(context.getLastResponse().path("name"), is(equalTo(name)));
    }

    @And("the holdings list contains ticker {string}")
    public void theHoldingsListContainsTicker(String ticker) {
        List<String> tickers = context.getLastResponse().jsonPath().getList("tickerSymbol");
        assertThat(tickers, hasItem(ticker));
    }

    @And("the holdings list has {int} items")
    public void theHoldingsListHasItems(int count) {
        List<?> list = context.getLastResponse().jsonPath().getList("$");
        assertThat(list, hasSize(count));
    }

    @And("the holding has {int} lots")
    public void theHoldingHasLots(int count) {
        List<Map<String, Object>> holdings = context.getLastResponse().jsonPath().getList("$");
        Map<String, Object> holding = holdings.stream()
                .filter(h -> ((Number) h.get("id")).longValue() == context.getHoldingId())
                .findFirst()
                .orElseThrow();

        List<?> lots = (List<?>) holding.get("lots");
        assertThat(lots, hasSize(count));
    }

    @And("the holding quantity is {int}")
    public void theHoldingQuantityIs(int quantity) {
        List<Map<String, Object>> holdings = context.getLastResponse().jsonPath().getList("$");
        Map<String, Object> holding = holdings.stream()
                .filter(h -> ((Number) h.get("id")).longValue() == context.getHoldingId())
                .findFirst()
                .orElseThrow();

        Number actual = (Number) holding.get("quantity");
        assertThat(actual.intValue(), is(quantity));
    }
}
