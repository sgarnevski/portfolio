package com.portfolio.it.steps;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TradeSteps {

    private final ScenarioContext context;

    public TradeSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("I have a portfolio with a holding")
    public void iHaveAPortfolioWithAHolding() {
        // Create portfolio
        Response portfolioResp = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("name", "Trade Test Portfolio"))
                .when()
                .post("/api/portfolios");

        assertThat(portfolioResp.statusCode(), is(201));
        Long portfolioId = portfolioResp.jsonPath().getLong("id");
        context.setPortfolioId(portfolioId);
        context.addCreatedPortfolioId(portfolioId);

        // Create holding
        Map<String, Object> holdingBody = new HashMap<>();
        holdingBody.put("tickerSymbol", "AAPL");
        holdingBody.put("name", "Apple Inc");
        holdingBody.put("assetClass", "EQUITY");
        holdingBody.put("currency", "USD");

        Response holdingResp = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(holdingBody)
                .when()
                .post("/api/portfolios/" + portfolioId + "/holdings");

        assertThat(holdingResp.statusCode(), is(201));
        context.setHoldingId(holdingResp.jsonPath().getLong("id"));
    }

    @When("I create a BUY trade for {int} shares at price {double}")
    public void iCreateABuyTrade(int quantity, double price) {
        createTrade("BUY", quantity, price, null);
    }

    @When("I create a BUY trade for {int} shares at price {double} with fee {double}")
    public void iCreateABuyTradeWithFee(int quantity, double price, double fee) {
        createTrade("BUY", quantity, price, fee);
    }

    @When("I create a SELL trade for {int} shares at price {double}")
    public void iCreateASellTrade(int quantity, double price) {
        createTrade("SELL", quantity, price, null);
    }

    private void createTrade(String type, int quantity, double price, Double fee) {
        Map<String, Object> body = new HashMap<>();
        body.put("date", LocalDate.now().toString());
        body.put("type", type);
        body.put("quantity", quantity);
        body.put("price", price);
        if (fee != null) {
            body.put("fee", fee);
        }

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/portfolios/" + context.getPortfolioId() + "/holdings/" + context.getHoldingId() + "/trades");

        context.setLastResponse(response);

        if (response.statusCode() == 201) {
            context.setTradeId(response.jsonPath().getLong("id"));
        }
    }

    @When("I list trades for the holding")
    public void iListTradesForTheHolding() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios/" + context.getPortfolioId() + "/holdings/" + context.getHoldingId() + "/trades");

        context.setLastResponse(response);
    }

    @When("I update the trade to {int} shares at price {double}")
    public void iUpdateTheTrade(int quantity, double price) {
        Map<String, Object> body = new HashMap<>();
        body.put("date", LocalDate.now().toString());
        body.put("type", "BUY");
        body.put("quantity", quantity);
        body.put("price", price);

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/portfolios/" + context.getPortfolioId() + "/holdings/" + context.getHoldingId() + "/trades/" + context.getTradeId());

        context.setLastResponse(response);
    }

    @When("I delete the trade")
    public void iDeleteTheTrade() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .delete("/api/portfolios/" + context.getPortfolioId() + "/holdings/" + context.getHoldingId() + "/trades/" + context.getTradeId());

        context.setLastResponse(response);
    }

    // Assertion steps

    @And("the trade type is {string}")
    public void theTradeTypeIs(String type) {
        assertThat(context.getLastResponse().path("type"), is(equalTo(type)));
    }

    @And("the trade quantity is {int}")
    public void theTradeQuantityIs(int quantity) {
        Number actual = context.getLastResponse().path("quantity");
        assertThat(actual.intValue(), is(quantity));
    }

    @And("the trade price is {double}")
    public void theTradepriceIs(double price) {
        Number actual = context.getLastResponse().path("price");
        assertThat(actual.doubleValue(), is(closeTo(price, 0.01)));
    }

    @And("the trade fee is {double}")
    public void theTradeFeeIs(double fee) {
        Number actual = context.getLastResponse().path("fee");
        assertThat(actual.doubleValue(), is(closeTo(fee, 0.01)));
    }

    @And("the trades list has at least {int} item")
    public void theTradesListHasAtLeastItems(int count) {
        List<?> list = context.getLastResponse().jsonPath().getList("$");
        assertThat(list.size(), is(greaterThanOrEqualTo(count)));
    }
}
