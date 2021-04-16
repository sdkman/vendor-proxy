Feature: Vendor header

  Scenario: An associated Vendor propagates a Vendor header to downstream services
    Given the zulu Consumer owned by john.doe@azul.com with Consumer Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 for candidate jmc
    And the header Consumer-Key b37048237fc587ef9caa29af9b57b762 is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    And the remote release service will return some CREATED response
    When posting JSON on the /release endpoint:
    """
          |{
          |  "candidate" : "jmc",
          |  "version" : "8.0.0",
          |  "platform": "LINUX_64",
          |  "url" : "http://hostname/zmc-binary-8.0.0.zip"
          |}
    """
    Then the status received is CREATED
    And the remote release service expects a Vendor header zulu


  Scenario: No associated Vendor does not propagate a Vendor header to downstream services
    Given the Consumer owned by john.doe@example.org with Consumer Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 for candidate groovy
    And the header Consumer-Key a4bf5bbb9feaa2713d99a3b52ab80024 is presented
    And the header Consumer-Token e0bf422d63d65ef1f4fe573a0d461d695edef45a541f07f3747ad37188329368 is presented
    And the remote release service will return some CREATED response
    When posting JSON on the /release endpoint:
    """
          |{
          |  "candidate" : "groovy",
          |  "version" : "2.3.6",
          |  "platform" : "UNIVERSAL",
          |  "url" : "http://hostname/groovy-binary-2.3.6.zip"
          |}
    """
    Then the status received is CREATED
    And the remote release service expects no Vendor header
