package com.portfolio.it.steps;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AllocationSteps {

    private final ScenarioContext context;

    public AllocationSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I set the following target allocations:")
    public void iSetTheFollowingTargetAllocations(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        List<Map<String, Object>> allocations = new ArrayList<>();

        for (Map<String, String> row : rows) {
            Map<String, Object> allocation = new HashMap<>();
            allocation.put("assetClass", row.get("assetClass"));
            allocation.put("targetPercentage", Double.parseDouble(row.get("targetPercentage")));
            allocations.add(allocation);
        }

        Map<String, Object> body = Map.of("allocations", allocations);

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/portfolios/" + context.getPortfolioId() + "/allocations");

        context.setLastResponse(response);
    }

    @When("I set empty target allocations")
    public void iSetEmptyTargetAllocations() {
        Map<String, Object> body = Map.of("allocations", List.of());

        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/portfolios/" + context.getPortfolioId() + "/allocations");

        context.setLastResponse(response);
    }

    @When("I get the target allocations")
    public void iGetTheTargetAllocations() {
        Response response = given()
                .baseUri(context.getBackendBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/portfolios/" + context.getPortfolioId() + "/allocations");

        context.setLastResponse(response);
    }

    // Assertion steps

    @And("the allocations list has {int} items")
    public void theAllocationsListHasItems(int count) {
        List<?> list = context.getLastResponse().jsonPath().getList("$");
        assertThat(list, hasSize(count));
    }

    @And("the allocations contain {string} at {int}")
    public void theAllocationsContainAt(String assetClass, int percentage) {
        List<Map<String, Object>> allocations = context.getLastResponse().jsonPath().getList("$");
        boolean found = allocations.stream()
                .anyMatch(a -> assetClass.equals(a.get("assetClass"))
                        && ((Number) a.get("targetPercentage")).intValue() == percentage);
        assertThat("Expected allocation " + assetClass + " at " + percentage + "%", found, is(true));
    }
}
