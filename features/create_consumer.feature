Feature: Create Consumer

  Scenario: A Consumer is assigned an Access Key and Access Token
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a POST request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the returned status is CREATED
    And the create response contains a consumerKey of value 5f202e7ab75f00af194c61cc07ae6b0c
    And the create response contains a valid consumerToken

  Scenario: Attempt Consumer creation without Admin Token
    Given the header Admin-Token invalid_token is presented
    When the /consumers endpoint receives a POST request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the returned status is FORBIDDEN
    And the response contains a status of value 403
    And the response contains message Not authorised to use this service.

  Scenario: An invalid payload is submitted for Consumer Creation
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a POST request:
    """
      |{ "remusnoc" : "yvoorg" }
    """
    Then the returned status is BAD_REQUEST
    And the response contains a status of value 400
    And the response contains message Malformed request body.

  Scenario: Consumer details are persisted
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a POST request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the create response contains a valid consumerToken
    And the Consumer groovy has been persisted
    And the persisted Consumer groovy has consumerKey 5f202e7ab75f00af194c61cc07ae6b0c
    And the persisted Consumer groovy has a valid sha256 representation of the consumerToken

  Scenario: A Consumer is not unique
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a POST request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the returned status is CREATED
    When the /consumers endpoint receives a POST request:
    """
      |{ "consumer" : "groovy" }
    """
    Then the returned status is CONFLICT
    And the response contains a status of value 409
    And the response contains message Duplicate key for consumer:
