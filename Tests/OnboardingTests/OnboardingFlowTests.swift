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

    func testDefaultProgressAccessibilityLabelNamesPageAndPageCount() {
        XCTAssertEqual(OnboardingFlow.defaultProgressAccessibilityLabel(2, 3), "Page 2 of 3")
    }

    func testDefaultProgressAccessibilityLabelExplainsAnEmptyFlow() {
        XCTAssertEqual(OnboardingFlow.defaultProgressAccessibilityLabel(0, 0), "No onboarding pages")
    }

    func testProgressAccessibilityLabelUsesDefaultForFirstPage() {
        let flow = OnboardingFlow(pages: makePages(count: 3), onComplete: {})

        XCTAssertEqual(flow.currentProgressAccessibilityLabel, "Page 1 of 3")
    }

    func testProgressAccessibilityLabelUsesAppSuppliedDescription() {
        let flow = OnboardingFlow(
            pages: makePages(count: 3),
            progressAccessibilityLabel: { page, pageCount in "Seite \(page) von \(pageCount)" },
            onComplete: {}
        )

        XCTAssertEqual(flow.currentProgressAccessibilityLabel, "Seite 1 von 3")
    }

    func testProgressAccessibilityLabelAsksTheAppToDescribeAnEmptyFlow() {
        let flow = OnboardingFlow(
            pages: [],
            progressAccessibilityLabel: { page, pageCount in "\(page)/\(pageCount)" },
            onComplete: {}
        )

        XCTAssertEqual(flow.currentProgressAccessibilityLabel, "0/0")
    }

    private func makePages(count: Int) -> [OnboardingPage] {
        (0..<count).map { index in
            OnboardingPage(
                id: "page-\(index)",
                title: "Title",
                subtitle: "Subtitle",
                systemImage: "sparkles",
                accentLabel: "Accent"
            )
        }
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
