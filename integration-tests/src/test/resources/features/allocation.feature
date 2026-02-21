Feature: Target Allocation Management
  As an authenticated user I want to set target allocations for my portfolio

  @authenticated
  Scenario: Set valid target allocations
    Given I have a portfolio named "Allocation Test"
    When I set the following target allocations:
      | assetClass  | targetPercentage |
      | EQUITY      | 60               |
      | BOND        | 25               |
      | COMMODITY   | 10               |
      | CASH        | 5                |
    Then the response status is 200
    And the allocations list has 4 items

  @authenticated
  Scenario: Get target allocations
    Given I have a portfolio named "Allocation Get Test"
    And I set the following target allocations:
      | assetClass  | targetPercentage |
      | EQUITY      | 70               |
      | BOND        | 30               |
    When I get the target allocations
    Then the response status is 200
    And the allocations contain "EQUITY" at 70
    And the allocations contain "BOND" at 30

  @authenticated
  Scenario: Update replaces previous allocations
    Given I have a portfolio named "Allocation Replace Test"
    And I set the following target allocations:
      | assetClass  | targetPercentage |
      | EQUITY      | 50               |
      | BOND        | 50               |
    When I set the following target allocations:
      | assetClass  | targetPercentage |
      | EQUITY      | 80               |
      | CASH        | 20               |
    Then the response status is 200
    And the allocations list has 2 items

  @authenticated
  Scenario: Reject allocations that do not sum to 100
    Given I have a portfolio named "Allocation Invalid Test"
    When I set the following target allocations:
      | assetClass  | targetPercentage |
      | EQUITY      | 50               |
      | BOND        | 30               |
    Then the response status is 400

  @authenticated
  Scenario: Reject empty allocations
    Given I have a portfolio named "Allocation Empty Test"
    When I set empty target allocations
    Then the response status is 400
