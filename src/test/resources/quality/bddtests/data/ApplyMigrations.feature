@applydbmigrations

  Feature: This is just a script to apply DB Migrations for the unit tests example site

    Scenario: Apply Migrations for Unit Test example site
      When I open website "http://localhost:8088/Departments"
      Then I click on element with ID "applyMigrations"
