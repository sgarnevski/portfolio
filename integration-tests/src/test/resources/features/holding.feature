Feature: Holding Management
  As an authenticated user I want to manage holdings within a portfolio

  @authenticated
  Scenario: Create a holding with currency
    Given I have a portfolio named "Holding Test"
    When I create a holding with ticker "AAPL" name "Apple Inc" assetClass "EQUITY" and currency "USD"
    Then the response status is 201
    And the holding ticker is "AAPL"
    And the holding currency is "USD"

  @authenticated
  Scenario: List holdings for a portfolio
    Given I have a portfolio named "Holding List Test"
    And I create a holding with ticker "MSFT" name "Microsoft" assetClass "EQUITY" and currency "USD"
    When I list holdings for the portfolio
    Then the response status is 200
    And the holdings list contains ticker "MSFT"

  @authenticated
  Scenario: Update a holding
    Given I have a portfolio named "Holding Update Test"
    And I create a holding with ticker "GOOG" name "Alphabet" assetClass "EQUITY" and currency "USD"
    When I update the holding name to "Alphabet Inc"
    Then the response status is 200
    And the holding name is "Alphabet Inc"

  @authenticated
  Scenario: Delete a holding
    Given I have a portfolio named "Holding Delete Test"
    And I create a holding with ticker "TSLA" name "Tesla" assetClass "EQUITY" and currency "USD"
    When I delete the holding
    Then the response status is 204

  @authenticated
  Scenario: Create holdings with different asset classes
    Given I have a portfolio named "Multi Asset Test"
    When I create a holding with ticker "BND" name "Bond Fund" assetClass "BOND" and currency "USD"
    Then the response status is 201
    When I create a holding with ticker "GLD" name "Gold ETF" assetClass "COMMODITY" and currency "USD"
    Then the response status is 201
    When I list holdings for the portfolio
    Then the response status is 200
    And the holdings list has 2 items
