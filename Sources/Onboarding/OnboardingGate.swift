import SwiftUI

public struct OnboardingGate<Content: View>: View {
    private let pages: [OnboardingPage]
    private let theme: OnboardingTheme
    private let allowsSkipping: Bool
    private let content: Content

    @AppStorage private var hasCompletedOnboarding: Bool

    public init(
        storageKey: String,
        pages: [OnboardingPage],
        theme: OnboardingTheme = .standard,
        allowsSkipping: Bool = true,
        @ViewBuilder content: () -> Content
    ) {
        self.pages = pages
        self.theme = theme
        self.allowsSkipping = allowsSkipping
        self.content = content()
        _hasCompletedOnboarding = AppStorage(wrappedValue: false, storageKey)
    }

    public var body: some View {
        Group {
            if hasCompletedOnboarding {
                content
            } else {
                OnboardingFlow(
                    pages: pages,
                    theme: theme,
                    allowsSkipping: allowsSkipping,
                    onComplete: { hasCompletedOnboarding = true }
                )
            }
        }
    }
}
