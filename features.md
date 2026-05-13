# Features (Gherkin)

Feature: Project feature tracking

  Scenario: Create and maintain feature specs
    Given the team is building product functionality
    When a feature is added, changed, or removed
    Then this file is updated first using Gherkin scenarios
    And each scenario reflects user-visible behavior

Feature: Keep implementation aligned with specs

  Scenario: Prevent drift between code and product intent
    Given implementation work is in progress
    When behavior differs from this file
    Then the code is brought into alignment with this spec
    Or the spec is updated with explicit approval

Feature: Onboarding completion controls

  Scenario: Require users to complete every onboarding page
    Given an app configures onboarding with skipping disabled
    When the onboarding flow is shown before the final page
    Then the skip action is not available
    And the status is "completed"
