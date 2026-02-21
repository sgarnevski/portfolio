Feature: Trade Management
  As an authenticated user I want to record trades for my holdings

  @authenticated
  Scenario: Record a BUY trade
    Given I have a portfolio with a holding
    When I create a BUY trade for 10 shares at price 150.00
    Then the response status is 201
    And the trade type is "BUY"
    And the trade quantity is 10
    And the trade price is 150.00

  @authenticated
  Scenario: Record a BUY trade with fee
    Given I have a portfolio with a holding
    When I create a BUY trade for 5 shares at price 200.00 with fee 9.99
    Then the response status is 201
    And the trade fee is 9.99

  @authenticated
  Scenario: List trades for a holding
    Given I have a portfolio with a holding
    And I create a BUY trade for 10 shares at price 100.00
    When I list trades for the holding
    Then the response status is 200
    And the trades list has at least 1 item

  @authenticated
  Scenario: Update a trade
    Given I have a portfolio with a holding
    And I create a BUY trade for 10 shares at price 100.00
    When I update the trade to 15 shares at price 105.00
    Then the response status is 200
    And the trade quantity is 15
    And the trade price is 105.00

  @authenticated
  Scenario: Delete a trade
    Given I have a portfolio with a holding
    And I create a BUY trade for 5 shares at price 50.00
    When I delete the trade
    Then the response status is 204

  @authenticated
  Scenario: Two BUY trades create two lots
    Given I have a portfolio with a holding
    And I create a BUY trade for 10 shares at price 100.00
    And I create a BUY trade for 5 shares at price 110.00
    When I get the holding details
    Then the holding has 2 lots
    And the holding quantity is 15

  @authenticated
  Scenario: SELL trade reduces lots FIFO
    Given I have a portfolio with a holding
    And I create a BUY trade for 10 shares at price 100.00
    And I create a BUY trade for 5 shares at price 110.00
    When I create a SELL trade for 8 shares at price 120.00
    And I get the holding details
    Then the holding quantity is 7
