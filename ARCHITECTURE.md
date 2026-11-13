# Project architecture

## Purpose

Onboarding is an open-source library that gives apps a ready-made first-run introduction flow. It is one product delivered as two native libraries: SwiftUI for iOS and Jetpack Compose for Android.

## Structure and boundaries

- `Sources/Onboarding/` is the iOS library. The root `Package.swift` declares only this target and no dependencies.
- `Tests/` is a separate Swift package with the iOS tests and snapshot references, so apps that add the library never download testing tools.
- `onboarding-android/` is the Android library with its unit, screenshot and instrumentation tests. Versions live in `gradle/libs.versions.toml`.
- `jitpack.yml` builds the Android release from a Git tag.
- [features.md](features.md) describes behavior and [evals.md](evals.md) records its test coverage.

The platforms share behavior, not code: each uses its own native API names, but capabilities and visible results must match.
The Android library is compiled so apps on Kotlin 2.1 and Jetpack Compose 1.8 can use it; do not raise that floor without approval.
[CONTRIBUTING.md](CONTRIBUTING.md) holds setup, snapshot and release details.

Keep this description current as the project evolves. Link detailed designs
instead of duplicating them here.
