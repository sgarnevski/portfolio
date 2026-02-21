Feature: Currency Management
  As an authenticated user I want to manage currencies

  @authenticated
  Scenario: List seed currencies
    When I list all currencies
    Then the response status is 200
    And the currency list contains code "USD"

  @authenticated
  Scenario: Create a new currency
    When I create a currency with code "TST" and name "Test Currency"
    Then the response status is 201
    And the currency code is "TST"
    And the currency name is "Test Currency"

  @authenticated
  Scenario: Update a currency
    Given I create a currency with code "UPD" and name "Update Me"
    When I update the currency name to "Updated Currency"
    Then the response status is 200
    And the currency name is "Updated Currency"

  @authenticated
  Scenario: Delete a currency
    Given I create a currency with code "DEL" and name "Delete Me"
    When I delete the currency
    Then the response status is 204
