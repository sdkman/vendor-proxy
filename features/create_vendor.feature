Feature: Create Vendor

  Scenario: A Vendor is assigned an Access Key and Access Token
    Given an Environment Variable "4e0a25e2559df6b6bea"
    And the valid Admin Token "4e0a25e2559df6b6bea" is presented
    When the Create Vendor endpoint "/vendors/create" is posted a request:
    """
      |{ "vendor" : "groovy" }
    """
    Then the status is "CREATED"
    And the payload contains "consumerKey" of value "5f202e7ab75f00af194c61cc07ae6b0c"
    And the payload contains a valid "consumerToken"

  @pending
  Scenario: Vendor details are persisted

  @pending
  Scenario: A Vendor is not unique

  @pending
  Scenario: Attempt Vendor creation without Admin Token

