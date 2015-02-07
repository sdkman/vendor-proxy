Feature: Create Vendor

  Scenario: A Vendor is assigned an Access Key and Access Token
    Given the Admin Token "default_token" is presented
    When the Create Vendor endpoint "/vendors/create" is posted a request:
    """
      |{ "vendor" : "groovy" }
    """
    Then the returned status is "CREATED"
    And the payload contains a consumerKey of value "5f202e7ab75f00af194c61cc07ae6b0c"
    And the payload contains a valid consumerToken

  Scenario: Attempt Vendor creation without Admin Token
    Given the Admin Token "invalid_token" is presented
    When the Create Vendor endpoint "/vendors/create" is posted a request:
    """
      |{ "vendor" : "groovy" }
    """
    Then the returned status is "FORBIDDEN"
    And the payload contains a statusCode of value 401
    And the payload contains message "Not authorised to use this service."

  @pending
  Scenario: Vendor details are persisted

  @pending
  Scenario: An invalid payload is submitted for Vendor Creation

  @pending
  Scenario: A Vendor is not unique
