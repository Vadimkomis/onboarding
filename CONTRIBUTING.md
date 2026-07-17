# Contributing

Thanks for taking the time to improve Onboarding.

## Development Setup

1. Clone the repository.
2. For iOS work, open the Swift package in Xcode. For Android work, open the repository in Android Studio.
3. Install Xcode with an iOS 17 or newer simulator for iOS development.
4. Use macOS or Linux with JDK 17 and Android SDK 36 for Android development. The Android library supports devices running API 23 or newer.
5. Run both platform checks before opening a pull request.

For iOS:

```sh
xcodebuild test -scheme Onboarding -destination 'platform=iOS Simulator,name=iPhone 16,OS=latest'
```

For Android:

```sh
./gradlew \
  :onboarding-android:lint \
  :onboarding-android:testDebugUnitTest \
  :onboarding-android:validateDebugScreenshotTest \
  :onboarding-android:assembleRelease
```

Android screenshot references live under `onboarding-android/src/screenshotTestDebug/reference`. After intentionally changing the Compose UI, review the rendered output and update the references with:

```sh
./gradlew :onboarding-android:updateDebugScreenshotTest
```

Commit reference changes only after visually approving all three images. Validation writes an HTML comparison report under `onboarding-android/build/reports/screenshotTest/preview/debug`.

With an API 23 or newer emulator or device connected, run the Compose and media lifecycle instrumentation tests separately:

```sh
./gradlew :onboarding-android:connectedDebugAndroidTest
```

To exercise the Android library from a separate app, publish the source checkout to the local Maven repository:

```sh
./gradlew :onboarding-android:publishToMavenLocal
```

The Android artifact is not published to Maven Central or another remote repository.

## Platform Parity

Onboarding is one product with native SwiftUI and Jetpack Compose implementations. Any user-visible behavior or public capability that applies to both platforms must be delivered on iOS and Android in the same pull request.

Platform-native APIs may use different names or types, but their capabilities and observable outcomes must remain equivalent. Before a shared change is complete:

1. Describe the platform-neutral behavior in `features.md`.
2. Implement it with native conventions on both platforms.
3. Add equivalent iOS and Android tests for the same outcomes and edge cases. Update both snapshot suites for visual changes.
4. Record the paired coverage and status in `evals.md`.
5. Run both platform test suites.

If an operating-system constraint makes equivalent behavior impossible, stop and obtain explicit approval before introducing a platform-specific exception.

## Pull Request Guidelines

- Keep changes focused on one problem or feature.
- Implement shared behavior and equivalent tests on both iOS and Android.
- Update `README.md` when public API or usage changes.
- Update `CHANGELOG.md` for user-visible changes.
- Preserve the supported iOS and Android APIs unless a compatibility change is intentional and documented.
- Ask for confirmation before adding a new production dependency.

## Release Notes

This package follows Semantic Versioning:

- Patch releases fix bugs without changing public API.
- Minor releases add backwards-compatible functionality.
- Major releases can include breaking changes.

Document notable changes in `CHANGELOG.md`.
