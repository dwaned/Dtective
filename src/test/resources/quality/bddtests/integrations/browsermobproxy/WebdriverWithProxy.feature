@BrowserMobProxyTest
@InternalSeleniumTest
Feature: Framework Browsermob Proxy

  Scenario: Record a browser interaction with Browsermob proxy
    
    Given I get configuration "BrowsermobProxy" and store it in data-store "BMPvalue"
    And I get configuration "IsRemoteInstance" and store it in data-store "BKRemoteInstance"
    And I get configuration "TestEnvironment" and store it in data-store "BKTestEnv"
    
    And I set configuration "BrowsermobProxy" to value "true"
    And I set configuration "IsRemoteInstance" to value "false"
    And I set configuration "TestEnvironment" to value "env-local"
    
    Given I open website "{unitTestSite}/Identity/Account/Login"
    When I start recording the browser interaction
    And I click by XPATH "//*[@id='Input_Email']"
    And I save the HAR file in "browsermobtestfile"
    And I stop recording the browser interaction
    
    Then the file "browsermobtestfile" exists
    
    And I set configuration "BrowsermobProxy" to value "{BMPvalue}"
    And I set configuration "IsRemoteInstance" to value "{BKRemoteInstance}"
    And I set configuration "TestEnvironment" to value "{BKTestEnv}"
