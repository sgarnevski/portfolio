package com.portfolio.it.steps;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class QuoteSteps {

    private final ScenarioContext context;

    public QuoteSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I search for ticker {string}")
    public void iSearchForTicker(String query) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .queryParam("q", query)
                .when()
                .get("/api/quotes/search");

        context.setLastResponse(response);
    }

    @When("I get a quote for symbol {string}")
    public void iGetAQuoteForSymbol(String symbol) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .when()
                .get("/api/quotes/" + symbol);

        context.setLastResponse(response);
    }

    @When("I get batch quotes for symbols {string}")
    public void iGetBatchQuotesForSymbols(String symbols) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .queryParam("symbols", symbols)
                .when()
                .get("/api/quotes");

        context.setLastResponse(response);
    }

    // Assertion steps

    @And("the search results contain symbol {string}")
    public void theSearchResultsContainSymbol(String symbol) {
        List<String> symbols = context.getLastResponse().jsonPath().getList("symbol");
        assertThat(symbols, hasItem(symbol));
    }

    @And("the quote symbol is {string}")
    public void theQuoteSymbolIs(String symbol) {
        assertThat(context.getLastResponse().path("symbol"), is(equalTo(symbol)));
    }

    @And("the quote has a market price")
    public void theQuoteHasAMarketPrice() {
        Number price = context.getLastResponse().path("regularMarketPrice");
        assertThat(price, is(notNullValue()));
        assertThat(price.doubleValue(), is(greaterThan(0.0)));
    }

    @And("the batch quotes list has at least {int} items")
    public void theBatchQuotesListHasAtLeastItems(int count) {
        List<?> list = context.getLastResponse().jsonPath().getList("$");
        assertThat(list.size(), is(greaterThanOrEqualTo(count)));
    }
}
