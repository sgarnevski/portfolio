Feature: Portfolio Management
  As an authenticated user I want to manage my portfolios

  @authenticated
  Scenario: Create a portfolio with name only
    When I create a portfolio with name "IT Test Portfolio"
    Then the response status is 201
    And the portfolio name is "IT Test Portfolio"

  @authenticated
  Scenario: Create a portfolio with all parameters
    When I create a portfolio with name "Full Portfolio" description "Integration test" driftThreshold 5.0 and cashBalance 10000.00
    Then the response status is 201
    And the portfolio name is "Full Portfolio"
    And the portfolio description is "Integration test"
    And the portfolio driftThreshold is 5.0
    And the portfolio cashBalance is 10000.00

  @authenticated
  Scenario: List portfolios
    Given I create a portfolio with name "List Test Portfolio"
    When I list all portfolios
    Then the response status is 200
    And the portfolio list is not empty

  @authenticated
  Scenario: Get a portfolio by ID
    Given I create a portfolio with name "Get Test Portfolio"
    When I get the portfolio by ID
    Then the response status is 200
    And the portfolio name is "Get Test Portfolio"

  @authenticated
  Scenario: Update a portfolio
    Given I create a portfolio with name "Before Update"
    When I update the portfolio name to "After Update"
    Then the response status is 200
    And the portfolio name is "After Update"

  @authenticated
  Scenario: Update portfolio cash balance
    Given I create a portfolio with name "Cash Test"
    When I update the portfolio cash balance to 5000.00
    Then the response status is 200
    And the portfolio cashBalance is 5000.00

  @authenticated
  Scenario: Delete a portfolio
    Given I create a portfolio with name "To Delete"
    When I delete the portfolio
    Then the response status is 204
    When I get the deleted portfolio by ID
    Then the response status is 404

  @authenticated
  Scenario: Get non-existent portfolio returns 404
    When I get portfolio with ID 999999
    Then the response status is 404
