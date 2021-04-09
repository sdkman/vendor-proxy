Feature: Create Consumer

  Scenario: A Consumer is assigned an Access Key and Access Token
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates" : []
      |}
    """
    Then the returned status is CREATED
    And the create response contains a consumerKey of value a4bf5bbb9feaa2713d99a3b52ab80024
    And the create response contains a valid consumerToken

  Scenario: Attempt Consumer creation without Admin Token
    Given the header Admin-Token invalid_token is presented
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates" : []
      |}
    """
    Then the returned status is FORBIDDEN
    And the response contains a status of value 403
    And the response contains message Not authorised to use this service.

  Scenario: An invalid payload is submitted for Consumer Creation
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "gloop" : "john.doe@example.org"
      |}
    """
    Then the returned status is BAD_REQUEST
    And the response contains a status of value 400
    And the response contains message Malformed request body.

  Scenario: Consumer details are persisted
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates" : []
      |}
    """
    Then the create response contains a valid consumerToken
    And the Consumer john.doe@example.org has been persisted
    And the persisted Consumer john.doe@example.org has consumerKey a4bf5bbb9feaa2713d99a3b52ab80024
    And the persisted Consumer john.doe@example.org has a valid sha256 representation of the consumerToken

  Scenario: A single candidate is assigned to a Consumer
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates": [
      |       "scala"
      |   ]
      |}
    """
    Then the returned status is CREATED
    And the Consumer john.doe@example.org has been persisted
    And the persisted Consumer john.doe@example.org has an associated candidate scala

  Scenario: Multiple candidates are assigned to a Consumer
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates": [
      |       "scala",
      |       "sbt"
      |   ]
      |}
    """
    Then the returned status is CREATED
    And the Consumer john.doe@example.org has been persisted
    And the persisted Consumer john.doe@example.org has an associated candidate scala
    And the persisted Consumer john.doe@example.org has an associated candidate sbt

  Scenario: A candidate is removed from a Consumer
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates": [
      |       "scala",
      |       "sbt"
      |   ]
      |}
    """
    Then the returned status is CREATED
    And the Consumer john.doe@example.org has been persisted
    And the persisted Consumer john.doe@example.org has an associated candidate scala
    And the persisted Consumer john.doe@example.org has an associated candidate sbt
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates": [
      |       "scala"
      |   ]
      |}
    """
    Then the returned status is CREATED
    And the persisted Consumer john.doe@example.org has an associated candidate scala
    And the persisted Consumer john.doe@example.org does not have an associated candidate sbt
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates": []
      |}
    """
    Then the returned status is CREATED
    And the persisted Consumer john.doe@example.org does not have an associated candidate scala
    And the persisted Consumer john.doe@example.org does not have an associated candidate sbt

  Scenario: An updated candidate results in a reissued token
    Given the header Admin-Token default_token is presented
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates": [
      |       "scala",
      |       "sbt"
      |   ]
      |}
    """
    Then the returned status is CREATED
    And the create response contains a valid consumerToken
    And the persisted Consumer john.doe@example.org has a valid sha256 representation of the consumerToken
    When the /consumers endpoint receives a PATCH request:
    """
      |{
      |   "consumer" : "john.doe@example.org",
      |   "candidates": [
      |       "scala"
      |   ]
      |}
    """
    Then the returned status is CREATED
    And the create response contains a reissued consumerToken
    And the persisted Consumer john.doe@example.org has a valid sha256 representation of the consumerToken
