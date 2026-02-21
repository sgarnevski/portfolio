Feature: Market Quotes
  As a user I want to search tickers and get quotes without authentication

  Scenario: Search for a ticker symbol
    When I search for ticker "AAPL"
    Then the response status is 200
    And the search results contain symbol "AAPL"

  Scenario: Get a single quote
    When I get a quote for symbol "MSFT"
    Then the response status is 200
    And the quote symbol is "MSFT"
    And the quote has a market price

  Scenario: Get batch quotes
    When I get batch quotes for symbols "AAPL,MSFT"
    Then the response status is 200
    And the batch quotes list has at least 2 items
