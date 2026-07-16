# Changelog

All notable changes to this package will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Native Jetpack Compose onboarding library for Android API 23 and newer.
- Android page media backed by Compose `ImageVector`, drawable resources, and video `Uri` values.
- Android completion persistence using a caller-provided storage key.
- Gradle tasks and CI coverage for Android lint, JVM tests, instrumentation test compilation, release AAR assembly, and local Maven publication.
- Host-side Android screenshot tests with checked-in references for the initial flow, single-page completion state, and completed gate content.
- Source-checkout and local Maven workflows for consuming the Android library during development.

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
