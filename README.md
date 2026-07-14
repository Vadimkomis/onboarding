# Onboarding

[![CI](https://github.com/Vadimkomis/onboarding/actions/workflows/ci.yml/badge.svg)](https://github.com/Vadimkomis/onboarding/actions/workflows/ci.yml)
![GitHub Tag](https://img.shields.io/github/v/tag/Vadimkomis/onboarding)
![License](https://img.shields.io/github/license/Vadimkomis/onboarding)
![Swift](https://img.shields.io/badge/Swift-5.9%2B-F05138?logo=swift&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-JVM-7F52FF?logo=kotlin&logoColor=white)
![Swift Package Manager](https://img.shields.io/badge/SPM-compatible-brightgreen)
![Platform](https://img.shields.io/badge/platform-iOS%2017%2B%20%7C%20Android%2023%2B-lightgrey)

Native SwiftUI and Jetpack Compose libraries for reusable first-run onboarding flows.

## Demo

This demo shows the iOS SwiftUI implementation.

![Onboarding demo](Docs/onboarding-demo.gif)

[Watch the full MP4 demo](Docs/onboarding-demo.mp4)

## Documentation

- [iOS Installation](#ios-installation)
- [iOS Quick Start](#ios-quick-start)
- [Android](#android)
- [Page Media](#page-media)
- [Custom Theme](#custom-theme)
- [Optional Skip](#optional-skip)
- [API Summary](#api-summary)
- [Changelog](CHANGELOG.md)
- [Contributing](CONTRIBUTING.md)

## Requirements

- iOS: iOS 17+, Swift 5.9+, and Swift Package Manager
- Android: API 23+ and Jetpack Compose
- Android development: macOS or Linux, JDK 17, and Android SDK 36

## iOS Installation

In Xcode:

1. Open your app project.
2. Select `File > Add Package Dependencies`.
3. Enter:

```text
git@github.com:Vadimkomis/onboarding.git
```

4. Add the `Onboarding` package product to your app target.

For public projects or CI, the HTTPS URL is also supported:

```text
https://github.com/Vadimkomis/onboarding.git
```

## iOS Quick Start

Import the package where you build your app's root view:

```swift
import Onboarding
import SwiftUI
```

Define the pages you want to show:

```swift
private let onboardingPages = [
    OnboardingPage(
        id: "welcome",
        title: "Welcome",
        subtitle: "Show users what your app helps them do.",
        media: .systemImage("sparkles"),
        accentLabel: "New"
    ),
    OnboardingPage(
        id: "preview",
        title: "Preview",
        subtitle: "Show an image from your app's asset catalog.",
        media: .image(name: "onboarding-preview"),
        accentLabel: "Visual"
    ),
    OnboardingPage(
        id: "demo",
        title: "Watch It Work",
        subtitle: "Autoplay a muted video when this page appears.",
        media: .video(
            url: Bundle.main.url(
                forResource: "onboarding-demo",
                withExtension: "mp4"
            )!
        ),
        accentLabel: "Demo"
    )
]
```

Wrap your main app view in `OnboardingGate`:

```swift
struct RootView: View {
    var body: some View {
        OnboardingGate(
            storageKey: "hasCompletedOnboarding",
            pages: onboardingPages
        ) {
            MainAppView()
        }
    }
}
```

`OnboardingGate` stores completion in `AppStorage` using the `storageKey`. Once the user taps skip or completes the final page, the gate shows your main content on future launches.

The number of screens is controlled by the `pages` array. Pass one `OnboardingPage` for a single-screen flow or as many pages as your onboarding needs.

## Page Media

The following media API is for the SwiftUI library. See [Android](#android) for the Compose equivalents.

Each `OnboardingPage` can show one media type:

```swift
media: .systemImage("location.fill")
media: .image(name: "onboarding-location")
media: .video(url: videoURL)
```

Use `.systemImage` for SF Symbols.

Use `.image(name:)` for images in the host app's asset catalog. The name should match the asset name. Images are preloaded and decoded when onboarding appears so the active page can show them immediately.

Use `.video(url:)` for a local or remote video URL. Videos are prepared when onboarding appears, show a first-frame poster while playback warms up, play muted, start when their page becomes active, pause when leaving the page, and loop after reaching the end.

Images and videos render directly in a tall portrait viewport, without an extra card or placeholder frame around them. Media fills the viewport so portrait assets use the available height and videos do not show side letterboxing.

For bundled videos, add the file to your app target and create the URL with:

```swift
let videoURL = Bundle.main.url(
    forResource: "onboarding-demo",
    withExtension: "mp4"
)!
```

## Custom Theme

Pass an `OnboardingTheme` when you want the flow to match your app:

```swift
let theme = OnboardingTheme(
    background: LinearGradient(
        colors: [.black, .blue],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    ),
    cardColor: .black.opacity(0.4),
    raisedColor: .white.opacity(0.12),
    highlightColor: .blue.opacity(0.2),
    primaryTextColor: .white,
    secondaryTextColor: .white.opacity(0.72),
    accentColor: .blue,
    warningColor: .orange,
    borderColor: .white.opacity(0.16),
    buttonGradient: LinearGradient(
        colors: [.blue, .purple],
        startPoint: .leading,
        endPoint: .trailing
    )
)

OnboardingGate(
    storageKey: "hasCompletedOnboarding",
    pages: onboardingPages,
    theme: theme,
    allowsSkipping: false
) {
    MainAppView()
}
```

If you do not pass a theme, the package uses `OnboardingTheme.standard`.

## Optional Skip

The skip button is enabled by default. Disable it when users must complete every onboarding page:

```swift
OnboardingGate(
    storageKey: "hasCompletedOnboarding",
    pages: onboardingPages,
    allowsSkipping: false
) {
    MainAppView()
}
```

## Direct Flow Usage

Use `OnboardingFlow` directly if your app wants to control completion state itself:

```swift
OnboardingFlow(
    pages: onboardingPages,
    continueTitle: "Next",
    completeTitle: "Get Started",
    skipTitle: "Skip"
) {
    markOnboardingComplete()
}
```

## Reset During Development

To show onboarding again for a specific storage key:

```swift
UserDefaults.standard.removeObject(forKey: "hasCompletedOnboarding")
```

Use a unique `storageKey` per app or per onboarding version if you need to show a refreshed flow to existing users.

## API Summary

The SwiftUI public API is:

```swift
OnboardingGate(
    storageKey: String,
    pages: [OnboardingPage],
    theme: OnboardingTheme = .standard,
    allowsSkipping: Bool = true,
    @ViewBuilder content: () -> Content
)

OnboardingPage(
    id: String,
    title: String,
    subtitle: String,
    media: OnboardingPageMedia,
    accentLabel: String
)

OnboardingFlow(
    pages: [OnboardingPage],
    theme: OnboardingTheme = .standard,
    continueTitle: String = "Continue",
    completeTitle: String = "Get Started",
    skipTitle: String = "Skip",
    allowsSkipping: Bool = true,
    onComplete: @escaping () -> Void
)
```

## Android

The `onboarding-android` module provides the same core onboarding flow as a native Jetpack Compose library for Android API 23 and newer.

### Android installation

Android artifacts are not published to Maven Central or another remote package repository. Clone this repository and publish the release artifact to your local Maven repository:

```sh
git clone https://github.com/Vadimkomis/onboarding.git
cd onboarding
./gradlew :onboarding-android:publishToMavenLocal
```

The repository's Gradle build includes the library as `:onboarding-android`. Its default local Maven coordinates are `com.vadimkomis:onboarding:1.1.0-SNAPSHOT`.

A module developed inside this source checkout can depend on the Gradle project directly:

```kotlin
dependencies {
    implementation(project(":onboarding-android"))
}
```

Add `mavenLocal()` to the consuming app's dependency repositories:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
```

Then add the dependency to the app module:

```kotlin
dependencies {
    implementation("com.vadimkomis:onboarding:1.1.0-SNAPSHOT")
}
```

To publish under a different local version, pass the same version to publication and dependency resolution:

```sh
./gradlew -PVERSION_NAME=1.1.0-local :onboarding-android:publishToMavenLocal
```

### Android quick start

Create Android-native pages using a Compose `ImageVector`, drawable resource, or video `Uri`:

```kotlin
val onboardingPages = listOf(
    OnboardingPage(
        id = "welcome",
        title = "Welcome",
        subtitle = "Show users what your app helps them do.",
        media = OnboardingPageMedia.Drawable(
            resourceId = R.drawable.onboarding_welcome,
            contentDescription = "Welcome illustration",
        ),
        accentLabel = "New",
    ),
    OnboardingPage(
        id = "demo",
        title = "Watch It Work",
        subtitle = "Show the feature in action.",
        media = OnboardingPageMedia.Video(
            uri = Uri.parse("android.resource://${BuildConfig.APPLICATION_ID}/${R.raw.onboarding_demo}"),
            contentDescription = "Feature demonstration",
        ),
        accentLabel = "Demo",
    ),
)
```

Gate the app's main content until onboarding is completed:

```kotlin
@Composable
fun RootScreen() {
    OnboardingGate(
        storageKey = "hasCompletedOnboarding",
        pages = onboardingPages,
    ) {
        MainAppScreen()
    }
}
```

The default completion store uses a private `SharedPreferences` file and the caller-provided `storageKey`. Pass an `OnboardingCompletionStore` when the app needs custom persistence or a deterministic test store.

Use `OnboardingFlow` directly when the app owns completion state:

```kotlin
OnboardingFlow(
    pages = onboardingPages,
    continueTitle = "Next",
    completeTitle = "Get Started",
    skipTitle = "Skip",
    allowsSkipping = true,
    onComplete = ::markOnboardingComplete,
)
```

Set `allowsSkipping = false` on either `OnboardingGate` or `OnboardingFlow` to require every page. Pass `OnboardingTheme.standard` explicitly or provide a custom `OnboardingTheme` to change the Compose colors and brushes.

Android media constructors are:

```kotlin
OnboardingPageMedia.Icon(imageVector = appIcon, contentDescription = "App icon")
OnboardingPageMedia.Drawable(resourceId = R.drawable.onboarding_image, contentDescription = "Preview")
OnboardingPageMedia.Video(uri = videoUri, contentDescription = "Demo video")
```

Video pages use Media3. Playback is muted and loops while the page is active, pauses when inactive, and releases the player with the lifecycle. First-frame poster extraction runs off the UI thread and falls back to an empty media viewport when no frame can be loaded.

Apps that use `http` or `https` video URIs must declare `android.permission.INTERNET` in their app manifest. The library does not add network permission to apps that only use local media.

## License

Onboarding is available under the MIT license. See [LICENSE](LICENSE) for details.
