package com.portfolio.it.hooks;

import com.portfolio.it.context.ScenarioContext;
import io.cucumber.java.Before;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthHooks {

    private final ScenarioContext context;

    public AuthHooks(ScenarioContext context) {
        this.context = context;
    }

    @Before("@authenticated")
    public void loginAsTestUser() {
        Response loginResponse = given()
                .baseUri(context.getAuthBaseUrl())
                .contentType(ContentType.JSON)
                .body(Map.of("username", "test", "password", "test123"))
                .when()
                .post("/api/auth/login");

        if (loginResponse.statusCode() != 200) {
            // User doesn't exist yet — register first
            loginResponse = given()
                    .baseUri(context.getAuthBaseUrl())
                    .contentType(ContentType.JSON)
                    .body(Map.of("username", "test", "email", "test@test.com", "password", "test123"))
                    .when()
                    .post("/api/auth/register");
        }

        context.setAuthToken(loginResponse.path("token"));
    }
}
