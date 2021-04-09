Feature: Support multi-candidate consumer

  Scenario: A consumer can publish to a single candidate
    Given the Consumer owned by groovy with Consumer Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 for candidate groovy
    And the header Consumer-Key 5f202e7ab75f00af194c61cc07ae6b0c is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    And the remote release service will return some CREATED response
    When posting JSON on the /release endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is CREATED
    And the remote release service expects a Consumer header groovy

  Scenario: A consumer can publish to multiple candidates
    Given the Consumer owned by groovy with Consumer Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 for candidate groovy,grails
    And the header Consumer-Key 5f202e7ab75f00af194c61cc07ae6b0c is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    And the remote release service will return some CREATED response
    When posting JSON on the /release endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is CREATED
    And the remote release service expects a Consumer header grails|groovy

  Scenario: A consumer can be orphaned from candidates
    Given the Consumer owned by groovy with Consumer Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 for no candidates
    And the header Consumer-Key 5f202e7ab75f00af194c61cc07ae6b0c is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    When posting JSON on the /release endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is FORBIDDEN