# Features (Gherkin)

Feature: Project feature tracking

  Scenario: Create and maintain feature specs
    Given the team is building product functionality
    When a feature is added, changed, or removed
    Then this file is updated first using Gherkin scenarios
    And each scenario reflects user-visible behavior
    And the status is "completed"

Feature: Keep implementation aligned with specs

  Scenario: Prevent drift between code and product intent
    Given implementation work is in progress
    When behavior differs from this file
    Then the code is brought into alignment with this spec
    Or the spec is updated with explicit approval
    And the status is "completed"

Feature: Cross-platform behavior parity

  Scenario: Deliver every shared behavior on iOS and Android
    Given Onboarding is delivered as native SwiftUI and Jetpack Compose libraries
    When user-visible behavior or a public capability is added, changed, or removed on either platform
    Then equivalent observable behavior is implemented on both platforms in the same change
    And equivalent automated tests cover both platforms, including paired snapshots for visual changes
    And native API shapes may differ while their capabilities remain equivalent
    And the change is not completed until both platform suites pass
    And the status is "completed"

  Scenario: Close existing platform-specific capability gaps
    Given a parity audit identified shared capabilities currently implemented only on Android
    When the existing onboarding behavior is brought into parity
    Then iOS provides equivalent completion-once, injectable persistence, saved-page restoration, media accessibility, and off-main-thread poster extraction behavior
    And equivalent iOS and Android tests cover those outcomes
    And the status is "in-progress"

Feature: Onboarding completion controls

  Scenario: Require users to complete every onboarding page
    Given an app configures onboarding with skipping disabled
    When the onboarding flow is shown before the final page
    Then the skip action is not available
    And the status is "completed"

Feature: Onboarding page composition

  Scenario: Configure any number of onboarding screens
    Given an app passes a pages array to the onboarding flow
    When the flow is displayed
    Then each page in the array is available as an onboarding screen
    And the status is "completed"

  Scenario: Show page media without an extra container
    Given an onboarding page uses image or video media
    When the page is displayed
    Then the media is shown directly in a tall portrait viewport without a placeholder card around it
    And image and video media fill the viewport without side letterboxing
    And image and video media are prepared before the active page displays them
    And video media shows a first-frame poster while playback warms up
    And the title and subtitle wrap without being clipped
    And the status is "completed"

Feature: Android onboarding support

  Scenario: Build Android support from the intended development hosts
    Given a contributor uses macOS or Linux
    When they build the Android onboarding library with the checked-in Gradle wrapper
    Then the Unix launcher is available without a Windows launcher
    And the status is "completed"

  Scenario: Integrate a native onboarding flow in an Android app
    Given an app targets Android API 23 or newer and uses Jetpack Compose
    When the app adds the onboarding-android library from a source checkout or the local Maven repository
    Then it can configure pages, themes, button titles, skipping, and completion behavior with Android-native APIs
    And page media accepts a Compose ImageVector, an Android drawable resource, or an Android Uri
    And the existing Swift Package Manager product remains available for iOS apps
    And the status is "completed"

  Scenario: Preserve Android onboarding state
    Given an Android onboarding flow is visible
    When the activity is recreated after a configuration or process state change
    Then the selected onboarding page is restored when saved state is available
    And the default completion store keeps completed onboarding hidden using the caller-provided storage key
    And callers can inject a completion store for testing or custom persistence
    And the status is "completed"

  Scenario: Present Android onboarding media safely
    Given an Android onboarding page uses an ImageVector icon, drawable resource, or video Uri
    When the page becomes active
    Then the media fills the same tall portrait viewport used by the onboarding design
    And video playback is muted, loops, pauses while inactive, and releases resources when removed
    And poster extraction and media preparation do not block the UI thread
    And the status is "completed"

  Scenario: Detect Android visual regressions
    Given approved Android reference images for the onboarding flow
    When host-side screenshot validation runs on macOS or Linux
    Then the initial multi-page flow, single-page completion state, and completed gate content match their references
    And the status is "completed"
