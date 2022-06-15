Feature: Security Testing by Zap
  I want to use zap to test the security of my application

  @tag1
  Scenario: Security scann for application 
    Given I open URL with ZAP browser
    When I check the URL
    Then I run spider scann in Homepage
    And I run active scann in Homepage
    And I run passive scann in Homepage