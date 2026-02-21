package com.portfolio.it.steps;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CurrencySteps {

    private final ScenarioContext context;

    public CurrencySteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I list all currencies")
    public void iListAllCurrencies() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/currencies");

        context.setLastResponse(response);
    }

    @When("I create a currency with code {string} and name {string}")
    public void iCreateACurrencyWithCodeAndName(String code, String name) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("code", code, "name", name))
                .when()
                .post("/api/currencies");

        context.setLastResponse(response);

        if (response.statusCode() == 201) {
            Long id = response.jsonPath().getLong("id");
            context.setCurrencyId(id);
            context.addCreatedCurrencyId(id);
        }
    }

    @When("I update the currency name to {string}")
    public void iUpdateTheCurrencyNameTo(String name) {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("code", context.getLastResponse().path("code"), "name", name))
                .when()
                .put("/api/currencies/" + context.getCurrencyId());

        context.setLastResponse(response);
    }

    @When("I delete the currency")
    public void iDeleteTheCurrency() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .delete("/api/currencies/" + context.getCurrencyId());

        context.setLastResponse(response);
        context.getCreatedCurrencyIds().remove(context.getCurrencyId());
    }

    // Assertion steps

    @And("the currency list contains code {string}")
    public void theCurrencyListContainsCode(String code) {
        List<String> codes = context.getLastResponse().jsonPath().getList("code");
        assertThat(codes, hasItem(code));
    }

    @And("the currency code is {string}")
    public void theCurrencyCodeIs(String code) {
        assertThat(context.getLastResponse().path("code"), is(equalTo(code)));
    }

    @And("the currency name is {string}")
    public void theCurrencyNameIs(String name) {
        assertThat(context.getLastResponse().path("name"), is(equalTo(name)));
    }
}
