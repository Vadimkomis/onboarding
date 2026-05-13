import XCTest

@testable import Onboarding

final class OnboardingPageTests: XCTestCase {
    func testOnboardingPageStoresConfiguration() {
        let page = OnboardingPage(
            id: "scan",
            title: "Scan",
            subtitle: "Upload a video",
            systemImage: "video",
            accentLabel: "Analysis"
        )

        XCTAssertEqual(page.id, "scan")
        XCTAssertEqual(page.title, "Scan")
        XCTAssertEqual(page.subtitle, "Upload a video")
        XCTAssertEqual(page.systemImage, "video")
        XCTAssertEqual(page.accentLabel, "Analysis")
    }
}
