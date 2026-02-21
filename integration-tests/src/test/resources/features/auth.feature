Feature: Authentication
  As a user I want to register, login, and manage my profile

  Scenario: Login with valid credentials
    Given the auth service is available
    When I login with username "test" and password "test123"
    Then the response status is 200
    And the response contains a JWT token
    And the response contains username "test"

  Scenario: Login with invalid credentials
    Given the auth service is available
    When I login with username "test" and password "wrongpassword"
    Then the response status is 403

  Scenario: Register a new user
    Given the auth service is available
    When I register with username "ituser_<random>" email "ituser_<random>@test.com" and password "pass123"
    Then the response status is 201
    And the response contains a JWT token

  @authenticated
  Scenario: Get current user profile
    When I get my profile
    Then the response status is 200
    And the response contains username "test"
    And the profile contains an email

  @authenticated
  Scenario: Update profile and revert
    When I get my profile
    And I save the original profile
    And I update my profile to a unique username
    Then the response status is 200
    When I revert my profile to the original values
    Then the response status is 200
    And the response contains username "test"

  @authenticated
  Scenario: Change password and revert
    When I change my password from "test123" to "newpass456"
    Then the response status is 200
    When I change my password from "newpass456" to "test123"
    Then the response status is 200

  @authenticated
  Scenario: Change password with wrong current password
    When I change my password from "wrongcurrent" to "newpass456"
    Then the response status is 400

  Scenario: Access protected endpoint without token
    When I get my profile without authentication
    Then the response status is 403
