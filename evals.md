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
- Status: in-progress
- Description: Validates the visual onboarding page layout where media is displayed directly in a tall portrait viewport instead of inside an additional container, while media is prepared early, videos have a first-frame poster, and title/subtitle text remains visible.
- Notes: The existing snapshots cover system-image pages and text layout. Direct image/video preload, poster, playback lifecycle, and cleanup coverage is still required before this eval can pass.

## Android

- Name: Android library builds and lints
- Status: passing
- Description: Validates that the Android onboarding module produces a release AAR and passes Android lint on the supported toolchain.
- Notes: The Unix Gradle wrapper runs on the supported macOS/Linux development hosts; no Windows launcher is included. Android lint passes with zero errors, all 18 JVM tests pass, the release AAR assembles, Maven-local publication succeeds, and a separate consumer app compiles against the published coordinate. CI runs lint, JVM tests, host-side screenshot validation, instrumentation source compilation, release assembly, and isolated Maven-local publication with JDK 17 and Android SDK 36 on pinned Ubuntu 24.04.

- Name: Android flow state matches the onboarding contract
- Status: passing
- Description: Validates empty, single-page, multi-page, clamped-index, advance, and last-page behavior on Android.
- Notes: All six deterministic `OnboardingFlowStateTest` cases pass as part of `testDebugUnitTest`.

- Name: Android navigation and skip policy
- Status: passing
- Description: Validates page navigation, configurable button labels, completion, and optional skip behavior in the Compose flow.
- Notes: All seven `OnboardingFlowTest` cases pass on a Pixel 8 Pro API 37.1 emulator, covering primary navigation, completion-once behavior, custom skip text, touch paging, progress semantics, saved-page restoration, and the empty flow. Connected-device tests remain a local release check rather than an emulator-free CI task.

- Name: Android completion and page state persist
- Status: passing
- Description: Validates keyed completion storage, key isolation, gate behavior, and restoration of the selected page after recreation.
- Notes: Passing tests cover storage persistence and key isolation, retry after a failed completion callback, completion-once behavior, four gate scenarios including the real default `SharedPreferences` store, and selected-page restoration through Compose saved state.

- Name: Android media follows lifecycle and layout rules
- Status: passing
- Description: Validates Android icon, drawable, and video media configuration plus active-only muted looping playback, poster handling, portrait cropping, and cleanup.
- Notes: `OnboardingPageTest` and `OnboardingFlowLayoutTest` cover native media configuration and portrait bounds. Six passing `OnboardingMediaTest` instrumentation cases cover drawable semantics, active/inactive lifecycle and cleanup, Media3 muted/repeating defaults, real-video poster extraction and preloading, deterministic first-frame state handling, and poster removal after actual Media3 playback renders its first frame.

- Name: Android visual snapshots match approved references
- Status: passing
- Description: Validates the rendered Compose UI for the initial multi-page flow, single-page completion state, and completed gate content at a fixed phone viewport.
- Notes: Three checked-in 1170×2532 reference PNGs render a fixed 390×844dp viewport at 3× density with en-US locale, fixed font scale, test-owned vector media, and the cross-platform snapshot theme. `validateDebugScreenshotTest` passes locally and is a required Android CI task with HTML diff reports uploaded on failure.

- Name: Cross-platform regression suite passes
- Status: passing
- Description: Validates that adding Android support does not change the existing iOS package behavior and that both platform test suites run independently.
- Notes: The unchanged iOS suite passes 18 tests, while Android passes 18 JVM, 3 host-side screenshot, and 17 connected-device tests plus lint, release AAR assembly, isolated Maven-local publication, and a separate consumer build. CI retains the iOS Simulator job and adds Android lint, JVM and screenshot tests, instrumentation source compilation, release assembly, and publication.
