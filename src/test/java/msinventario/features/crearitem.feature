@Item
Feature: Perform a item creation

  Scenario: Perform item creation with valid details
    Given an item with valid details
      | codigo      | ASD       |
      | nombre      | empanadas |
    When request is submitted for item creation
    Then verify that the Item HTTP response is 200
    And a item id is returned

  Scenario: Perform a failed item creation
    Given an item with invalid details
      | codigo      | ASD       |
    When request is submitted for item creation
    Then verify that the Item HTTP response is 500