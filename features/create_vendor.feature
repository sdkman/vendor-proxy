Feature: Create Vendor

  Scenario: A Vendor is assigned an Access Key and Access Token
    Given the valid Admin Token "default_token" is presented
    When the Create Vendor endpoint "/vendors/create" is posted a request:
    """
      |{ "vendor" : "groovy" }
    """
    Then the returned status is "CREATED"
    And the payload contains a consumerKey of value "5f202e7ab75f00af194c61cc07ae6b0c"
    And the payload contains a valid consumerToken

  @pending
  Scenario: Vendor details are persisted

  @pending
  Scenario: A Vendor is not unique

  @pending
  Scenario: Attempt Vendor creation without Admin Token

