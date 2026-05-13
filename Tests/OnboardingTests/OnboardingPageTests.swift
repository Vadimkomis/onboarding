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
        XCTAssertEqual(page.media, .systemImage("video"))
        XCTAssertEqual(page.systemImage, "video")
        XCTAssertEqual(page.accentLabel, "Analysis")
    }

    func testOnboardingPageStoresImageMediaConfiguration() {
        let page = OnboardingPage(
            id: "preview",
            title: "Preview",
            subtitle: "See how the feature looks.",
            media: .image(name: "onboarding-preview"),
            accentLabel: "Visual"
        )

        XCTAssertEqual(page.media, .image(name: "onboarding-preview"))
        XCTAssertEqual(page.systemImage, "")
    }

    func testOnboardingPageStoresVideoMediaConfiguration() throws {
        let url = try XCTUnwrap(URL(string: "https://example.com/onboarding.mp4"))
        let page = OnboardingPage(
            id: "demo",
            title: "Demo",
            subtitle: "Watch the flow in action.",
            media: .video(url: url),
            accentLabel: "Demo"
        )

        XCTAssertEqual(page.media, .video(url: url))
        XCTAssertEqual(page.systemImage, "")
    }

    func testOnboardingPageUsesIdForIdentifiableConformance() {
        let page = OnboardingPage(
            id: "permissions",
            title: "Enable Access",
            subtitle: "Turn on the permissions needed to continue.",
            systemImage: "lock.shield",
            accentLabel: "Privacy"
        )

        XCTAssertEqual(page.id, "permissions")
    }

    func testOnboardingPageEquatableComparesAllConfiguration() {
        let page = OnboardingPage(
            id: "welcome",
            title: "Welcome",
            subtitle: "Learn the basics.",
            systemImage: "sparkles",
            accentLabel: "New"
        )

        XCTAssertEqual(page, page)
        XCTAssertNotEqual(
            page,
            OnboardingPage(
                id: "welcome",
                title: "Welcome",
                subtitle: "Learn the basics.",
                media: .image(name: "welcome"),
                accentLabel: "Updated"
            )
        )
    }
}
