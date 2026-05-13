# Onboarding

A small SwiftUI onboarding package for apps that need a reusable first-run flow.

## Usage

```swift
import Onboarding

let pages = [
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
        subtitle: "Show an image from the host app's asset catalog.",
        media: .image(name: "onboarding-preview"),
        accentLabel: "Visual"
    ),
    OnboardingPage(
        id: "demo",
        title: "Watch It Work",
        subtitle: "Autoplay a muted video when this page appears.",
        media: .video(url: Bundle.main.url(forResource: "onboarding-demo", withExtension: "mp4")!),
        accentLabel: "Demo"
    )
]

OnboardingGate(storageKey: "hasCompletedOnboarding", pages: pages) {
    MainAppView()
}
```

Pass a custom `OnboardingTheme` to match the host app's design system.
