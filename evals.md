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

- Name: Swift CI cache excludes compiler module state
- Status: passing
- Description: Confirms that the Swift dependency cache does not restore DerivedData or Clang module files built against a different Xcode SDK.
- Notes: Before restoring the cache, an inline shell check configures DerivedData under `RUNNER_TEMP` and rejects paths nested under the cached package-source path. The job caches only `.build/SourcePackages`, keyed on the test package manifest and pins, preserving the regression coverage without a separate script file.

- Name: Swift package declares no test-only dependencies
- Status: passing
- Description: Confirms that apps adding the Onboarding package resolve no extra packages because the iOS tests live in their own package.
- Notes: The root `Package.swift` has no `dependencies`. `Tests/Package.swift` depends on the root package by path and on `swift-snapshot-testing`; all 30 iOS tests pass from it with the `OnboardingTests-Package` scheme. A consuming app's build resolved only its own packages.

- Name: Shared behavior changes require paired platform delivery
- Status: passing
- Description: Validates that newly added, changed, or removed shared behavior cannot be considered complete without equivalent native implementation and automated coverage on iOS and Android.
- Notes: The gate is enforced through `AGENTS.md`, `features.md`, `CONTRIBUTING.md`, and the issue and pull request templates. CI executes iOS tests and Android JVM/screenshot checks for every pull request; device behavior changes additionally require `connectedDebugAndroidTest` evidence. This eval governs intentionally scoped behavior changes and does not retroactively define unrequested product capabilities.

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

- Name: Android quick start avoids generated BuildConfig
- Status: passing
- Description: Validates that the documented Android video resource URI uses the runtime application package without requiring generated BuildConfig fields.
- Notes: `ReadmeQuickStartTest` extracts the Android quick start, rejects `BuildConfig`, and verifies the exact runtime-package URI interpolation.

- Name: Standard highlight color matches across platforms
- Status: passing
- Description: Validates that the public standard themes use #1D4ED8 at 20 percent opacity on both iOS and Android.
- Notes: Exact Android and iOS assertions lock the standard highlight to the same color and opacity.

- Name: Android drawable loading is asynchronous and configuration-aware
- Status: passing
- Description: Validates that drawable resources are decoded off the UI thread and reloaded for configuration, context, or explicitly versioned in-place theme changes.
- Notes: Instrumentation coverage verifies background loading, density/locale/ui-mode reloads, replacement contexts, retained-context theme versioning, immutable theme snapshots, bounded bitmap-only caching, isolated cached drawable instances, and independent video-poster preloading during drawable reloads.

- Name: Legacy Android video poster extraction avoids unbounded decoding
- Status: passing
- Description: Validates that API 23 through 26 never requests a full-resolution poster frame when the platform cannot bound the decode.
- Notes: Deterministic JVM coverage proves API 23 through 26 skips poster extraction and API 27 or newer requests only a 640×1024 scaled frame. Legacy playback falls through to the player-rendered first frame.

- Name: Android library builds and lints
- Status: passing
- Description: Validates that the Android onboarding module produces a release AAR and passes Android lint on the supported toolchain.
- Notes: The Unix Gradle wrapper runs on the supported macOS/Linux development hosts; no Windows launcher is included. Android lint passes with zero errors, all 25 JVM tests pass, the release AAR assembles, Maven-local publication succeeds, and a separate consumer app compiles against the published coordinate. CI runs lint, JVM tests, host-side screenshot validation, instrumentation source compilation, release assembly, and isolated Maven-local publication with JDK 17 and Android SDK 36 on pinned Ubuntu 24.04.

- Name: Android flow state matches the onboarding contract
- Status: passing
- Description: Validates empty, single-page, multi-page, clamped-index, advance, and last-page behavior on Android.
- Notes: All six deterministic `OnboardingFlowStateTest` cases pass as part of `testDebugUnitTest`.

- Name: Android navigation and skip policy
- Status: passing
- Description: Validates page navigation, configurable button labels, completion, and optional skip behavior in the Compose flow.
- Notes: All eleven `OnboardingFlowTest` cases pass on a Pixel 8 Pro emulator, covering primary navigation, app-supplied progress descriptions, the themed button label, completion-once behavior, custom skip text, touch paging, progress semantics, saved-page restoration, and the empty flow. Connected-device tests remain a local release check rather than an emulator-free CI task.

- Name: Android completion and page state persist
- Status: passing
- Description: Validates keyed completion storage, key isolation, gate behavior, and restoration of the selected page after recreation.
- Notes: Passing tests cover storage persistence and key isolation, retry after a failed completion callback, completion-once behavior, four gate scenarios including the real default `SharedPreferences` store, and selected-page restoration through Compose saved state.

- Name: Android media follows lifecycle and layout rules
- Status: passing
- Description: Validates Android icon, drawable, and video media configuration plus active-only muted looping playback, poster handling, portrait cropping, and cleanup.
- Notes: Media coverage includes background drawable decoding, automatic configuration/context reloads, explicit retained-context theme invalidation, independent preloading, skipped unbounded poster extraction on API 23 through 26, player lifecycle, portrait cropping, and cleanup.

- Name: Android visual snapshots match approved references
- Status: passing
- Description: Validates the rendered Compose UI for the initial multi-page flow, single-page completion state, and completed gate content at a fixed phone viewport.
- Notes: Three checked-in 1170×2532 reference PNGs render a fixed 390×844dp viewport at 3× density with en-US locale, fixed font scale, test-owned vector media, and the cross-platform snapshot theme. `validateDebugScreenshotTest` passes locally and is a required Android CI task with HTML diff reports uploaded on failure.

- Name: Primary button label follows the theme
- Status: passing
- Description: Validates that a theme's button text color colors the primary button label on both platforms and that omitting it keeps a white label.
- Notes: iOS `OnboardingThemeTests` and Android `OnboardingThemeTest` cover the stored, standard, and default colors. Android `OnboardingFlowTest` also reads the rendered label color on a device. The iOS rendered label is covered only by the unchanged default-theme snapshots.

- Name: Progress is announced with an app-supplied description
- Status: passing
- Description: Validates that assistive technology receives the app's description of the current page and page count, with English text by default and 0 of 0 for an empty flow.
- Notes: iOS `OnboardingFlowTests` cover the default text, an app-supplied closure, and the empty flow through `currentProgressAccessibilityLabel`. Android covers the default text in `OnboardingProgressDescriptionTest` and the announced semantics in `OnboardingFlowTest`. The iOS VoiceOver modifiers are not asserted through a hosted accessibility tree.

- Name: Page media can be described or hidden
- Status: passing
- Description: Validates that pages carry an optional media description on both platforms and that undescribed media is hidden from assistive technology.
- Notes: iOS `OnboardingPageTests` cover storage, the default, and equality for `mediaAccessibilityLabel`; Android media tests cover `contentDescription`. The iOS VoiceOver modifiers are not asserted through a hosted accessibility tree.

- Name: Android library supports the documented consumer floor
- Status: passing
- Description: Validates that an app on Kotlin 2.1 and Compose 1.8 can adopt the library without its Kotlin standard library, Compose, or lifecycle versions being raised.
- Notes: The library compiles with Kotlin language and API level 2.1 against standard library 2.1.20, Compose BOM 2025.04.01, and lifecycle 2.8.7. Verified manually by building an app on Kotlin 2.1.20 and Compose Multiplatform 1.8.0 against a locally published build; CI does not yet build an older-toolchain consumer.

- Name: Android library resolves from JitPack
- Status: planned
- Description: Validates that a pushed release tag builds on JitPack and resolves as `com.github.Vadimkomis:onboarding`.
- Notes: `jitpack.yml` builds with JDK 17 and publishes with the JitPack group and version. Local publication under those coordinates resolves from a consumer app; the hosted build can only be checked after a tag is pushed, starting with a release candidate.

- Name: Cross-platform regression suite passes
- Status: passing
- Description: Validates that adding Android support does not change the existing iOS package behavior and that both platform test suites run independently.
- Notes: iOS passes 30 tests, while Android passes 25 JVM, 3 host-side screenshot, and 29 connected-device tests plus lint, release AAR assembly, isolated Maven-local publication, and a separate consumer build. CI retains the iOS Simulator job and adds Android lint, JVM and screenshot tests, instrumentation source compilation, release assembly, and publication.
