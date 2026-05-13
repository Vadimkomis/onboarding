# Onboarding

[![CI](https://github.com/Vadimkomis/onboarding/actions/workflows/ci.yml/badge.svg)](https://github.com/Vadimkomis/onboarding/actions/workflows/ci.yml)
![GitHub Release](https://img.shields.io/github/v/release/Vadimkomis/onboarding)
![License](https://img.shields.io/github/license/Vadimkomis/onboarding)
![Swift](https://img.shields.io/badge/Swift-5.9%2B-F05138?logo=swift&logoColor=white)
![Swift Package Manager](https://img.shields.io/badge/SPM-compatible-brightgreen)
![Platform](https://img.shields.io/badge/platform-iOS%2017%2B-lightgrey)

A small SwiftUI package for reusable first-run onboarding flows.

## Requirements

- iOS 17+
- Swift Package Manager

## Installation

In Xcode:

1. Open your app project.
2. Select `File > Add Package Dependencies`.
3. Enter:

```text
git@github.com:Vadimkomis/onboarding.git
```

4. Add the `Onboarding` package product to your app target.

## Quick Start

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

Each `OnboardingPage` can show one media type:

```swift
media: .systemImage("location.fill")
media: .image(name: "onboarding-location")
media: .video(url: videoURL)
```

Use `.systemImage` for SF Symbols.

Use `.image(name:)` for images in the host app's asset catalog. The name should match the asset name.

Use `.video(url:)` for a local or remote video URL. Videos play muted, start when their page becomes active, stop when leaving the page, and loop after reaching the end.

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

## License

Onboarding is available under the MIT license. See [LICENSE](LICENSE) for details.
