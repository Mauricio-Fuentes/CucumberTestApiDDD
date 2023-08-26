@Producto
Feature: Perform a account creation

  Scenario: Perform account creation with valid details
    Given a account with valid details
      | name | Juan Perez         |
      | number | 6     |
      | city      | Santa Cruz |
      | type      | Ahorro |
    When request is submitted for account creation
    Then verify that the HTTP response is 200
    And a account id is returned