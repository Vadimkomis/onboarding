import SwiftUI
import XCTest

@testable import Onboarding

final class OnboardingFlowTests: XCTestCase {
    func testOnboardingFlowStoresCustomConfiguration() throws {
        let pages = [
            OnboardingPage(
                id: "welcome",
                title: "Welcome",
                subtitle: "Start here.",
                systemImage: "sparkles",
                accentLabel: "New"
            )
        ]
        let theme = makeTheme()
        var didComplete = false

        let flow = OnboardingFlow(
            pages: pages,
            theme: theme,
            continueTitle: "Next",
            completeTitle: "Done",
            skipTitle: "Later",
            allowsSkipping: false,
            onComplete: { didComplete = true }
        )

        XCTAssertEqual(try mirroredValue("pages", in: flow), pages)
        XCTAssertEqual(try mirroredValue("continueTitle", in: flow), "Next")
        XCTAssertEqual(try mirroredValue("completeTitle", in: flow), "Done")
        XCTAssertEqual(try mirroredValue("skipTitle", in: flow), "Later")
        XCTAssertFalse(try mirroredValue("allowsSkipping", in: flow))

        let completion: () -> Void = try mirroredValue("onComplete", in: flow)
        completion()
        XCTAssertTrue(didComplete)
        _ = flow.body
    }

    func testOnboardingFlowUsesDefaultTitles() throws {
        let flow = OnboardingFlow(
            pages: [],
            onComplete: {}
        )

        XCTAssertEqual(try mirroredValue("continueTitle", in: flow), "Continue")
        XCTAssertEqual(try mirroredValue("completeTitle", in: flow), "Get Started")
        XCTAssertEqual(try mirroredValue("skipTitle", in: flow), "Skip")
        XCTAssertTrue(try mirroredValue("allowsSkipping", in: flow))
        _ = flow.body
    }

    private func makeTheme() -> OnboardingTheme {
        OnboardingTheme(
            background: LinearGradient(
                colors: [.black, .blue],
                startPoint: .top,
                endPoint: .bottom
            ),
            cardColor: .gray,
            raisedColor: .secondary,
            highlightColor: .blue.opacity(0.2),
            primaryTextColor: .white,
            secondaryTextColor: .secondary,
            accentColor: .blue,
            warningColor: .orange,
            borderColor: .white.opacity(0.1),
            buttonGradient: LinearGradient(
                colors: [.blue, .purple],
                startPoint: .leading,
                endPoint: .trailing
            )
        )
    }
}
