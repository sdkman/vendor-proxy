  Feature: Proxy the Release API

  Background:
    Given a the Vendor "groovy" with Consumer Token "e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368"

  Scenario: Client successfully Releases a new Version
    Given the Consumer Key "5f202e7ab75f00af194c61cc07ae6b0c" is presented
    And the Consumer Token "e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368" is presented
    And the remote release service returns a "CREATED" response:
    """
          |{
          |  "status": 201,
          |  "id": "5426b98dba78e60054fe482f",
          |  "message": "released groovy version: 2.3.6"
          |}
    """
    When posting JSON on the "/release" endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is "CREATED"
    And the response is:
    """
          |{
          |  "status": 201,
          |  "id": "5426b98dba78e60054fe482f",
          |  "message": "released groovy version: 2.3.6"
          |}
    """

  Scenario: Client is denied access to Release due to invalid Consumer Key
    Given the Consumer Key "invalid_key" is presented
    And the Consumer Token "e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368" is presented
    When posting JSON on the "/release" endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is "FORBIDDEN"
    And the response is:
    """
          |{
          |  "status": 403,
          |  "message": "Not authorised to use this service."
          |}
    """

  Scenario: Client is denied access to Release due to invalid Consumer Token
    Given the Consumer Key "5f202e7ab75f00af194c61cc07ae6b0c" is presented
    And the Consumer Token "invalid_token" is presented
    When posting JSON on the "/release" endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is "FORBIDDEN"
    And the response is:
    """
          |{
          |  "status": 403,
          |  "message": "Not authorised to use this service."
          |}
    """

  Scenario: Client fails Release due to Conflict with existing Version
    Given the Consumer Key "5f202e7ab75f00af194c61cc07ae6b0c" is presented
    And the Consumer Token "e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368" is presented
    And the remote release service returns a "CONFLICT" response:
    """
          |{
          |  "status": 409,
          |  "message": "duplicate candidate version: groovy 2.3.6"
          |}
    """
    When posting JSON on the "/release" endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is "CONFLICT"
    And the response is:
    """
          |{
          |  "status": 409,
          |  "message": "duplicate candidate version: groovy 2.3.6"
          |}
    """

  Scenario: Client fails Release due to Bad Request
    Given the Consumer Key "5f202e7ab75f00af194c61cc07ae6b0c" is presented
    And the Consumer Token "e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368" is presented
    And the remote release service returns a "BAD_REQUEST" response:
    """
          |{
          |  "status": 400,
          |  "message": "Could not read JSON: Unrecognized field \"cand\" (class net.gvmtool.release.request.ReleaseRequest), not marked as ignorable (3 known properties: \"version\", \"candidate\", \"url\"])\ at [Source: org.apache.catalina.connector.CoyoteInputStream@31e3c705; line: 1, column: 62] (through reference chain: net.gvmtool.release.request.ReleaseRequest[\"cand\"]); nested exception is com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field \"cand\" (class net.gvmtool.release.request.ReleaseRequest), not marked as ignorable (3 known properties: \"version\", \"candidate\", \"url\"])\ at [Source: org.apache.catalina.connector.CoyoteInputStream@31e3c705; line: 1, column: 62] (through reference chain: net.gvmtool.release.request.ReleaseRequest[\"cand\"])"
          |}
    """
    When posting JSON on the "/release" endpoint:
    """
          |{
          |  "cand" : "groovy",
          |  "ver" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is "BAD_REQUEST"
    And the response is:
    """
          |{
          |  "status": 400,
          |  "message": "Malformed JSON payload"
          |}
    """

  Scenario: Client fails Release due to remote Internal Server Error
    Given the Consumer Key "5f202e7ab75f00af194c61cc07ae6b0c" is presented
    And the Consumer Token "e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368" is presented
    And the remote release service returns a "INTERNAL_SERVER_ERROR" response:
    """
          |{
          |  "status": 500,
          |  "message": "Internal Server Error"
          |}
    """
    When posting JSON on the "/release" endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is "INTERNAL_SERVER_ERROR"
    And the response is:
    """
          |{
          |  "status": 500,
          |  "message": "Internal Server Error"
          |}
    """

  Scenario: Bad Gateway because Release Access Token incorrectly configured
    Given the Consumer Key "5f202e7ab75f00af194c61cc07ae6b0c" is presented
    And the Consumer Token "e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368" is presented
    And the remote release service returns a "FORBIDDEN" response:
    """
          |{
          |  "status": 403,
          |  "message": "Invalid consumer token provided."
          |}
    """
    When posting JSON on the "/release" endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is "BAD_GATEWAY"
    And the response is:
    """
          |{
          |  "status": 502,
          |  "message": "Remote service unavailable"
          |}
    """

  Scenario: Bad Gateway when Release API URL incorrectly configured
    Given the Consumer Key "5f202e7ab75f00af194c61cc07ae6b0c" is presented
    And the Consumer Token "e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368" is presented
    And the remote release service is unavailable
    When posting JSON on the "/release" endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is "BAD_GATEWAY"
    And the response is:
    """
          |{
          |  "status": 502,
          |  "message": "Remote service unavailable"
          |}
    """
