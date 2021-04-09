Feature: Delete Consumer

  Scenario: An existing Consumer is revoked
    Given the header Admin-Token default_token is presented
    And an existing Consumer owned by john.doe@example.org for candidates groovy,grails
    When the /consumers/john.doe@example.org endpoint receives a DELETE request
    Then the returned status is OK
    And the delete response contains a consumerKey of value a4bf5bbb9feaa2713d99a3b52ab80024
    And the delete response contains an owner of value john.doe@example.org
    And the delete response contains message consumer deleted

  Scenario: A non-existent Consumer is revoked
    Given the header Admin-Token default_token is presented
    And no existing Consumer named groovy
    When the /consumers/nobody@example.org endpoint receives a DELETE request
    Then the returned status is NOT_FOUND

  Scenario: Attempt Consumer deletion without Admin Token
    When the /consumers/john.doe@example.org endpoint receives a DELETE request
    Then the returned status is FORBIDDEN