# Evals

## Status Legend
- `planned`
- `in-progress`
- `passing`
- `failing`
- `deprecated`

## Eval Template

- Name:
- Status:
- Description:
- Notes:

## Example

- Name: Core happy path returns expected result
- Status: planned
- Description: Validates the default user flow from input to successful completion.
- Notes: Add fixtures for edge input once core flow is passing.

## Onboarding

- Name: Skip can be disabled
- Status: passing
- Description: Validates that `allowsSkipping` is stored by gate and flow configuration so apps can require full onboarding completion.
- Notes: Covered by `OnboardingFlowTests.testOnboardingFlowStoresCustomConfiguration` and `OnboardingGateTests.testOnboardingGateStoresConfiguration`.

- Name: Page count follows configuration
- Status: passing
- Description: Validates that onboarding flow state is derived from the provided page count instead of assuming a fixed three-screen flow.
- Notes: Covered by `OnboardingFlowStateTests.testInitialStateStartsAtFirstPage`, `testAdvanceMovesForwardUntilLastPage`, `testEmptyStateIsComplete`, and `OnboardingSnapshotTests.testOnboardingFlowSinglePageCompleteState`.

- Name: Media renders without placeholder card
- Status: passing
- Description: Validates the visual onboarding page layout where media is displayed directly in a tall portrait viewport instead of inside an additional container, while title and subtitle text remain visible.
- Notes: Covered by `OnboardingSnapshotTests.testOnboardingFlowInitialScreen` and `testOnboardingFlowSinglePageCompleteState`.
