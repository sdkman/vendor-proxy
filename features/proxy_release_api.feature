  Feature: Proxy the Release API

  Background:
    Given the Consumer owned by john.doe@example.org with Consumer Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 for candidate groovy

  Scenario: Client successfully Releases a new Version
    Given the header Consumer-Key a4bf5bbb9feaa2713d99a3b52ab80024 is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    And the remote release service will return a CREATED response:
    """
          |{
          |  "status": 201,
          |  "id": "5426b98dba78e60054fe482f",
          |  "message": "released groovy version: 2.3.6"
          |}
    """
    When posting JSON on the /versions endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is CREATED
    And the response is:
    """
          |{
          |  "status": 201,
          |  "id": "5426b98dba78e60054fe482f",
          |  "message": "released groovy version: 2.3.6"
          |}
    """
    And the remote release service expects payload and appropriate headers:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """

  Scenario: Client is denied access to Release due to invalid Consumer Key
    Given the header Consumer-Key invalid_key is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    When posting JSON on the /versions endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is FORBIDDEN
    And the response is:
    """
          |{
          |  "status": 403,
          |  "message": "Not authorised to use this service."
          |}
    """
    And the remote release service expects NO posts

  Scenario: Client is denied access to Release due to invalid Consumer Token
    Given the header Consumer-Key a4bf5bbb9feaa2713d99a3b52ab80024 is presented
    And the header Consumer-Token invalid_token is presented
    When posting JSON on the /versions endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is FORBIDDEN
    And the response is:
    """
          |{
          |  "status": 403,
          |  "message": "Not authorised to use this service."
          |}
    """
    And the remote release service expects NO posts

  Scenario: Client fails Release due to Conflict with existing Version
    Given the header Consumer-Key a4bf5bbb9feaa2713d99a3b52ab80024 is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    And the remote release service will return a CONFLICT response:
    """
          |{
          |  "status": 409,
          |  "message": "duplicate candidate version: groovy 2.3.6"
          |}
    """
    When posting JSON on the /versions endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is CONFLICT
    And the response is:
    """
          |{
          |  "status": 409,
          |  "message": "duplicate candidate version: groovy 2.3.6"
          |}
    """
    And the remote release service expects payload and appropriate headers:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """

  Scenario: Client fails Release due to remote Internal Server Error
    Given the header Consumer-Key a4bf5bbb9feaa2713d99a3b52ab80024 is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    And the remote release service will return a INTERNAL_SERVER_ERROR response:
    """
          |{
          |  "status": 500,
          |  "message": "Internal Server Error"
          |}
    """
    When posting JSON on the /versions endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is INTERNAL_SERVER_ERROR
    And the response is:
    """
          |{
          |  "status": 500,
          |  "message": "Internal Server Error"
          |}
    """
    And the remote release service expects payload and appropriate headers:
      """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """

  Scenario: Bad Gateway because Release Access Token incorrectly configured
    Given the header Consumer-Key a4bf5bbb9feaa2713d99a3b52ab80024 is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    And the remote release service will return a FORBIDDEN response:
    """
          |{
          |  "status": 403,
          |  "message": "Blah blah blah."
          |}
    """
    When posting JSON on the /versions endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is FORBIDDEN
    And the response is:
    """
          |{
          |  "status": 403,
          |  "message": "Blah blah blah."
          |}
    """

  Scenario: Bad Gateway when Release API URL incorrectly configured
    Given the header Consumer-Key a4bf5bbb9feaa2713d99a3b52ab80024 is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    And the remote release service is unavailable
    When posting JSON on the /versions endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is NOT_FOUND

  Scenario: Client successfully Releases a new Version using legacy headers
      Given the header consumer_key a4bf5bbb9feaa2713d99a3b52ab80024 is presented
      And the header consumer_token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
      And the remote release service will return a CREATED response:
    """
          |{
          |  "status": 201,
          |  "id": "5426b98dba78e60054fe482f",
          |  "message": "released groovy version: 2.3.6"
          |}
    """
      When posting JSON on the /versions endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
      Then the status received is CREATED
