import SwiftUI
import XCTest

@testable import Onboarding

final class OnboardingThemeTests: XCTestCase {
    func testOnboardingThemeStoresCustomConfiguration() {
        let theme = OnboardingTheme(
            background: LinearGradient(
                colors: [.red, .blue],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            ),
            cardColor: .white,
            raisedColor: .gray,
            highlightColor: .yellow.opacity(0.5),
            primaryTextColor: .black,
            secondaryTextColor: .secondary,
            accentColor: .green,
            warningColor: .orange,
            borderColor: .black.opacity(0.2),
            buttonGradient: LinearGradient(
                colors: [.green, .mint],
                startPoint: .leading,
                endPoint: .trailing
            )
        )

        XCTAssertNotNil(theme.background)
        XCTAssertNotNil(theme.cardColor)
        XCTAssertNotNil(theme.raisedColor)
        XCTAssertNotNil(theme.highlightColor)
        XCTAssertNotNil(theme.primaryTextColor)
        XCTAssertNotNil(theme.secondaryTextColor)
        XCTAssertNotNil(theme.accentColor)
        XCTAssertNotNil(theme.warningColor)
        XCTAssertNotNil(theme.borderColor)
        XCTAssertNotNil(theme.buttonGradient)
    }

    func testStandardThemeIsAvailable() {
        let theme = OnboardingTheme.standard

        XCTAssertNotNil(theme.background)
        XCTAssertNotNil(theme.cardColor)
        XCTAssertNotNil(theme.raisedColor)
        XCTAssertNotNil(theme.highlightColor)
        XCTAssertNotNil(theme.primaryTextColor)
        XCTAssertNotNil(theme.secondaryTextColor)
        XCTAssertNotNil(theme.accentColor)
        XCTAssertNotNil(theme.warningColor)
        XCTAssertNotNil(theme.borderColor)
        XCTAssertNotNil(theme.buttonGradient)
    }
}
