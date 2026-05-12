# ReusableOnboarding

A small SwiftUI onboarding package for apps that need a reusable first-run flow.

## Usage

```swift
import ReusableOnboarding

let pages = [
    OnboardingPage(
        id: "welcome",
        title: "Welcome",
        subtitle: "Show users what your app helps them do.",
        systemImage: "sparkles",
        accentLabel: "New"
    )
]

OnboardingGate(storageKey: "hasCompletedOnboarding", pages: pages) {
    MainAppView()
}
```

Pass a custom `OnboardingTheme` to match the host app's design system.
