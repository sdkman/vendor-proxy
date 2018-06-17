Feature: Delete Consumer

  Scenario: An existing Consumer is revoked
    Given the header Admin-Token default_token is presented
    And an existing Consumer named groovy
    When the /consumers/groovy endpoint receives a DELETE request
    Then the returned status is OK
    And the delete response contains a consumerKey of value 5f202e7ab75f00af194c61cc07ae6b0c
    And the delete response contains a name of value groovy
    And the delete response contains message consumer deleted

  Scenario: A non-existent Consumer is revoked
    Given the header Admin-Token default_token is presented
    And no existing Consumer named groovy
    When the /consumers/groovy endpoint receives a DELETE request
    Then the returned status is NOT_FOUND

  Scenario: Attempt Consumer deletion without Admin Token
    When the /consumers/groovy endpoint receives a DELETE request
    Then the returned status is FORBIDDEN