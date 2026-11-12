# Changelog

All notable changes to this package will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- `buttonTextColor` theme color for the primary button label on iOS and Android, defaulting to white.
- App-supplied progress descriptions for assistive technology on iOS (`progressAccessibilityLabel`) and Android (`progressDescription`), so apps can localize "Page 1 of 3".
- Optional `mediaAccessibilityLabel` on iOS pages, matching Android's media `contentDescription`.
- JitPack publication for the Android library as `com.github.Vadimkomis:onboarding`.

### Changed

- The Android library now supports apps built with Kotlin 2.1 and Jetpack Compose 1.8. It no longer raises a consuming app's Kotlin standard library, Compose, or lifecycle versions.
- VoiceOver now announces the iOS progress indicator, matching TalkBack on Android.
- iOS page media without a `mediaAccessibilityLabel` is hidden from VoiceOver instead of announcing its symbol or asset name.
- Android apps compiled against 1.1.0 must be recompiled, because `OnboardingFlow` and `OnboardingTheme` gained optional parameters.

## [1.1.0] - 2026-07-22

### Added

- Native Jetpack Compose onboarding library for Android API 23 and newer.
- Android page media backed by Compose `ImageVector`, drawable resources, and video `Uri` values.
- Android completion persistence using a caller-provided storage key.
- Gradle tasks and CI coverage for Android lint, JVM tests, instrumentation test compilation, release AAR assembly, and local Maven publication.
- Host-side Android screenshot tests with checked-in references for the initial flow, single-page completion state, and completed gate content.
- Source-checkout and local Maven workflows for consuming the Android library during development.
- Android quick-start resource URIs built from the runtime package without generated `BuildConfig` fields.
- Background Android drawable loading with configuration and in-place theme invalidation.
- Allocation-safe Android video posters using scaled extraction on API 27 and newer and player-rendered fallback on API 23–26.
- Android standard highlight color matching the iOS palette.

## [1.0.0] - 2026-05-14

### Added

- Reusable SwiftUI onboarding gate and flow for iOS apps.
- Configurable onboarding pages with app-defined identifiers, titles, subtitles, media, and accent labels.
- Support for SF Symbol, asset catalog image, and local or remote video page media.
- Tall portrait media layout for image and video onboarding content.
- Muted autoplay video playback with looping.
- First-frame video posters while playback warms up.
- Preloading and decoding for image media.
- Early video asset preparation and reusable video players.
- Configurable theming through `OnboardingTheme`.
- Optional skip button with `allowsSkipping`.
- Configurable continue, completion, and skip button titles.
- Flexible page count based on the provided pages array.
- `OnboardingGate` persistence through a caller-provided `storageKey`.
- GitHub Actions CI for iOS tests.
- MIT license.
