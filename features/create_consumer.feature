Feature: Create Consumer

  Scenario: A Consumer is assigned an Access Key and Access Token
    Given the "Admin-Token" "default_token" is presented
    When the Create Consumer endpoint "/consumers" is posted a request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the returned status is "CREATED"
    And the payload contains a consumerKey of value "5f202e7ab75f00af194c61cc07ae6b0c"
    And the payload contains a valid consumerToken

  Scenario: Attempt Consumer creation without Admin Token
    Given the "Admin-Token" "invalid_token" is presented
    When the Create Consumer endpoint "/consumers" is posted a request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the returned status is "FORBIDDEN"
    And the payload contains a status of value 403
    And the payload contains message "Not authorised to use this service."

  Scenario: An invalid payload is submitted for Consumer Creation
    Given the "Admin-Token" "default_token" is presented
    When the Create Consumer endpoint "/consumers" is posted a request:
    """
      |{ "remusnoc" : "yvoorg" }
    """
    Then the returned status is "BAD_REQUEST"
    And the payload contains a status of value 400
    And the payload contains message "Malformed request body."

  Scenario: Consumer details are persisted
    Given the "Admin-Token" "default_token" is presented
    When the Create Consumer endpoint "/consumers" is posted a request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the Consumer "groovy" has been persisted
    And the persisted Consumer "groovy" has consumerKey "5f202e7ab75f00af194c61cc07ae6b0c"
    And the persisted Consumer "groovy" has a valid consumerToken

  @pending
  Scenario: A Consumer is not unique
    Given the "Admin-Token" "default_token" is presented
    When the Create Consumer endpoint "/consumers" is posted a request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the returned status is "CREATED"
    When the Create Consumer endpoint "/consumers" is posted a request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the returned status is "CONFLICT"
    And the payload contains a status of value 409
    And the payload contains message "Duplicate key for consumer:"
