import SwiftUI
import XCTest

@testable import Onboarding

final class OnboardingGateTests: XCTestCase {
    func testOnboardingGateStoresConfiguration() throws {
        let pages = [
            OnboardingPage(
                id: "first",
                title: "First Step",
                subtitle: "Introduce the flow.",
                systemImage: "1.circle",
                accentLabel: "Start"
            )
        ]

        let gate = OnboardingGate(
            storageKey: "test.onboarding.completed",
            pages: pages,
            theme: .standard,
            allowsSkipping: false
        ) {
            Text("Main content")
        }

        XCTAssertEqual(try mirroredValue("pages", in: gate), pages)
        XCTAssertFalse(try mirroredValue("allowsSkipping", in: gate))
        let content: Text = try mirroredValue("content", in: gate)
        XCTAssertNotNil(content)
        _ = gate.body
    }
}
