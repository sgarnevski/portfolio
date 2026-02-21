Feature: Portfolio Rebalancing
  As an authenticated user I want to calculate rebalance recommendations

  @authenticated
  Scenario: Full portfolio rebalance
    Given I have a portfolio named "Rebalance Test" with cashBalance 10000.00
    And I create a holding with ticker "AAPL" name "Apple Inc" assetClass "EQUITY" and currency "USD"
    And I create a BUY trade for 10 shares at price 150.00
    And I set the following target allocations:
      | assetClass  | targetPercentage |
      | EQUITY      | 60               |
      | BOND        | 30               |
      | CASH        | 10               |
    When I request a full rebalance
    Then the response status is 200
    And the rebalance response contains portfolioId
    And the rebalance response contains allocations
    And the rebalance response contains calculatedAt

  @authenticated
  Scenario: Cash rebalance with additional amount
    Given I have a portfolio named "Cash Rebalance Test" with cashBalance 5000.00
    And I create a holding with ticker "MSFT" name "Microsoft" assetClass "EQUITY" and currency "USD"
    And I create a BUY trade for 5 shares at price 300.00
    And I set the following target allocations:
      | assetClass  | targetPercentage |
      | EQUITY      | 70               |
      | CASH        | 30               |
    When I request a cash rebalance with amount 2000.00
    Then the response status is 200
    And the rebalance response contains portfolioId
    And the rebalance response contains allocations
