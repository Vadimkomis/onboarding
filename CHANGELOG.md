# Changelog

All notable changes to this package will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

### Fixed

- Removed the extra placeholder frame around image and video media.
- Prevented video side letterboxing in the onboarding media viewport.
- Prevented tall media from clipping title and subtitle text.
- Improved snapshot test stability on iOS CI.
