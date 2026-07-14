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
  :onboarding-android:assembleRelease
```

With an API 23 or newer emulator or device connected, run the Compose and media lifecycle instrumentation tests separately:

```sh
./gradlew :onboarding-android:connectedDebugAndroidTest
```

To exercise the Android library from a separate app, publish the source checkout to the local Maven repository:

```sh
./gradlew :onboarding-android:publishToMavenLocal
```

The Android artifact is not published to Maven Central or another remote repository.

## Pull Request Guidelines

- Keep changes focused on one problem or feature.
- Add or update tests for behavior changes.
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
