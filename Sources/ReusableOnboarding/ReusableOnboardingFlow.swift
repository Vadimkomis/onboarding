import SwiftUI

public struct ReusableOnboardingFlow: View {
    private let pages: [OnboardingPage]
    private let theme: OnboardingTheme
    private let continueTitle: String
    private let completeTitle: String
    private let skipTitle: String
    private let onComplete: () -> Void

    @State private var selectedIndex = 0

    public init(
        pages: [OnboardingPage],
        theme: OnboardingTheme = .standard,
        continueTitle: String = "Continue",
        completeTitle: String = "Get Started",
        skipTitle: String = "Skip",
        onComplete: @escaping () -> Void
    ) {
        self.pages = pages
        self.theme = theme
        self.continueTitle = continueTitle
        self.completeTitle = completeTitle
        self.skipTitle = skipTitle
        self.onComplete = onComplete
    }

    public var body: some View {
        ZStack {
            theme.background
                .ignoresSafeArea()

            VStack(spacing: 20) {
                progressIndicator

                TabView(selection: $selectedIndex) {
                    ForEach(Array(pages.enumerated()), id: \.element.id) { index, page in
                        OnboardingPageView(page: page, theme: theme)
                            .tag(index)
                    }
                }
                .onboardingPageTabStyle()

                controls
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 18)
        }
    }

    private var progressIndicator: some View {
        HStack(spacing: 8) {
            ForEach(pages.indices, id: \.self) { index in
                Capsule()
                    .fill(index == selectedIndex ? theme.accentColor : theme.raisedColor)
                    .frame(width: index == selectedIndex ? 30 : 8, height: 8)
                    .animation(.easeInOut(duration: 0.2), value: selectedIndex)
            }
        }
        .frame(maxWidth: .infinity, alignment: .center)
        .accessibilityHidden(true)
    }

    private var controls: some View {
        VStack(spacing: 12) {
            Button(action: advance) {
                Text(isLastPage ? completeTitle : continueTitle)
            }
            .buttonStyle(OnboardingPrimaryButtonStyle(theme: theme))

            Button(action: onComplete) {
                Text(skipTitle)
                    .font(.subheadline.weight(.semibold))
                    .foregroundColor(theme.secondaryTextColor)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
            }
            .opacity(isLastPage ? 0 : 1)
            .disabled(isLastPage)
        }
    }

    private var isLastPage: Bool {
        selectedIndex >= pages.count - 1
    }

    private func advance() {
        guard !isLastPage else {
            onComplete()
            return
        }

        withAnimation(.easeInOut(duration: 0.24)) {
            selectedIndex += 1
        }
    }
}

private extension View {
    @ViewBuilder
    func onboardingPageTabStyle() -> some View {
        #if os(iOS)
        tabViewStyle(.page(indexDisplayMode: .never))
        #else
        self
        #endif
    }
}

private struct OnboardingPageView: View {
    let page: OnboardingPage
    let theme: OnboardingTheme

    var body: some View {
        VStack(spacing: 28) {
            Spacer(minLength: 12)

            ZStack {
                RoundedRectangle(cornerRadius: 32, style: .continuous)
                    .fill(theme.cardColor)
                    .overlay(
                        RoundedRectangle(cornerRadius: 32, style: .continuous)
                            .stroke(theme.borderColor, lineWidth: 1)
                    )
                    .frame(maxWidth: 300, maxHeight: 300)

                VStack(spacing: 18) {
                    Image(systemName: page.systemImage)
                        .font(.system(size: 64, weight: .semibold))
                        .foregroundColor(theme.accentColor)
                        .frame(width: 128, height: 128)
                        .background(Circle().fill(theme.highlightColor))

                    Text(page.accentLabel.uppercased())
                        .font(.caption.weight(.bold))
                        .foregroundColor(theme.warningColor)
                }
            }
            .frame(height: 320)

            VStack(spacing: 12) {
                Text(page.title)
                    .font(.largeTitle.weight(.bold))
                    .foregroundColor(theme.primaryTextColor)
                    .multilineTextAlignment(.center)
                    .minimumScaleFactor(0.82)

                Text(page.subtitle)
                    .font(.body)
                    .foregroundColor(theme.secondaryTextColor)
                    .multilineTextAlignment(.center)
                    .lineSpacing(3)
                    .frame(maxWidth: 330)
            }

            Spacer(minLength: 12)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct OnboardingPrimaryButtonStyle: ButtonStyle {
    let theme: OnboardingTheme

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.headline.weight(.semibold))
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
            .background(theme.buttonGradient)
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .stroke(theme.borderColor, lineWidth: 1)
            )
            .opacity(configuration.isPressed ? 0.88 : 1)
            .scaleEffect(configuration.isPressed ? 0.99 : 1)
    }
}
