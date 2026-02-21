package com.portfolio.it.steps;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AuthSteps {

    private final ScenarioContext context;
    private String randomSuffix;

    public AuthSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the auth service is available")
    public void theAuthServiceIsAvailable() {
        // no-op, just a readability step
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) {
        Response response = given()
                .baseUri(context.getAuthBaseUrl())
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .when()
                .post("/api/auth/login");

        context.setLastResponse(response);

        if (response.statusCode() == 200) {
            context.setAuthToken(response.path("token"));
        }
    }

    @When("I register with username {string} email {string} and password {string}")
    public void iRegisterWithUsernameEmailAndPassword(String username, String email, String password) {
        randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        String actualUsername = username.replace("<random>", randomSuffix);
        String actualEmail = email.replace("<random>", randomSuffix);

        Response response = given()
                .baseUri(context.getAuthBaseUrl())
                .contentType(ContentType.JSON)
                .body(Map.of("username", actualUsername, "email", actualEmail, "password", password))
                .when()
                .post("/api/auth/register");

        context.setLastResponse(response);
    }

    @When("I get my profile")
    public void iGetMyProfile() {
        Response response = given()
                .baseUri(context.getAuthBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .when()
                .get("/api/auth/me");

        context.setLastResponse(response);
    }

    @When("I get my profile without authentication")
    public void iGetMyProfileWithoutAuthentication() {
        Response response = given()
                .baseUri(context.getAuthBaseUrl())
                .when()
                .get("/api/auth/me");

        context.setLastResponse(response);
    }

    @And("I save the original profile")
    public void iSaveTheOriginalProfile() {
        Response resp = context.getLastResponse();
        context.setOriginalUsername(resp.path("username"));
        context.setOriginalEmail(resp.path("email"));
    }

    @When("I update my profile to a unique username")
    public void iUpdateMyProfileToAUniqueUsername() {
        String uniqueName = "test_" + UUID.randomUUID().toString().substring(0, 6);
        String uniqueEmail = uniqueName + "@test.com";
        context.setOriginalPassword("test123");

        Response response = given()
                .baseUri(context.getAuthBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("username", uniqueName, "email", uniqueEmail))
                .when()
                .put("/api/auth/me");

        context.setLastResponse(response);

        // Re-login with new username to get a valid token
        if (response.statusCode() == 200) {
            Response loginResp = given()
                    .baseUri(context.getAuthBaseUrl())
                    .contentType(ContentType.JSON)
                    .body(Map.of("username", uniqueName, "password", "test123"))
                    .when()
                    .post("/api/auth/login");
            context.setAuthToken(loginResp.path("token"));
        }
    }

    @When("I revert my profile to the original values")
    public void iRevertMyProfileToTheOriginalValues() {
        Response response = given()
                .baseUri(context.getAuthBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("username", context.getOriginalUsername(), "email", context.getOriginalEmail()))
                .when()
                .put("/api/auth/me");

        context.setLastResponse(response);

        // Re-login with reverted username
        if (response.statusCode() == 200) {
            Response loginResp = given()
                    .baseUri(context.getAuthBaseUrl())
                    .contentType(ContentType.JSON)
                    .body(Map.of("username", context.getOriginalUsername(), "password", "test123"))
                    .when()
                    .post("/api/auth/login");
            context.setAuthToken(loginResp.path("token"));
        }
    }

    @When("I change my password from {string} to {string}")
    public void iChangeMyPasswordFromTo(String currentPassword, String newPassword) {
        Response response = given()
                .baseUri(context.getAuthBaseUrl())
                .header("Authorization", "Bearer " + context.getAuthToken())
                .contentType(ContentType.JSON)
                .body(Map.of("currentPassword", currentPassword, "newPassword", newPassword))
                .when()
                .post("/api/auth/me/change-password");

        context.setLastResponse(response);
    }

    // Shared assertion steps

    @Then("the response status is {int}")
    public void theResponseStatusIs(int statusCode) {
        assertThat(context.getLastResponse().statusCode(), is(statusCode));
    }

    @And("the response contains a JWT token")
    public void theResponseContainsAJwtToken() {
        String token = context.getLastResponse().path("token");
        assertThat(token, is(notNullValue()));
        assertThat(token.split("\\.").length, is(3));
    }

    @And("the response contains username {string}")
    public void theResponseContainsUsername(String username) {
        assertThat(context.getLastResponse().path("username"), is(equalTo(username)));
    }

    @And("the profile contains an email")
    public void theProfileContainsAnEmail() {
        String email = context.getLastResponse().path("email");
        assertThat(email, is(notNullValue()));
        assertThat(email, containsString("@"));
    }
}
