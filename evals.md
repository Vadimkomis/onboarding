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
