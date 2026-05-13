import SnapshotTesting
import SwiftUI
import XCTest

@testable import Onboarding

@MainActor
final class OnboardingSnapshotTests: XCTestCase {
    func testOnboardingFlowInitialScreen() {
        let view = OnboardingFlow(
            pages: snapshotPages,
            theme: snapshotTheme,
            onComplete: {}
        )
        .frame(width: 390, height: 844)

        assertViewSnapshot(of: view, named: "initial-screen")
    }

    func testOnboardingFlowSinglePageCompleteState() {
        let view = OnboardingFlow(
            pages: [snapshotPages[0]],
            theme: snapshotTheme,
            completeTitle: "Start Tracking",
            onComplete: {}
        )
        .frame(width: 390, height: 844)

        assertViewSnapshot(of: view, named: "single-page-complete")
    }

    func testOnboardingGateCompletedContent() {
        let storageKey = "snapshot.onboarding.completed"
        UserDefaults.standard.set(true, forKey: storageKey)
        defer { UserDefaults.standard.removeObject(forKey: storageKey) }

        let view = OnboardingGate(
            storageKey: storageKey,
            pages: snapshotPages,
            theme: snapshotTheme
        ) {
            ZStack {
                Color.white
                Text("Main App")
                    .font(.system(size: 36, weight: .bold))
                    .foregroundColor(.black)
            }
        }
        .frame(width: 390, height: 844)

        assertViewSnapshot(of: view, named: "completed-content")
    }
}

private func assertViewSnapshot(
    of view: some View,
    named name: String,
    file: StaticString = #filePath,
    testName: String = #function,
    line: UInt = #line
) {
    let size = CGSize(width: 390, height: 844)

    #if os(macOS)
    let controller = NSHostingController(rootView: view)
    controller.view.frame = CGRect(origin: .zero, size: size)

    assertSnapshot(
        of: controller,
        as: .image(size: size),
        named: name,
        file: file,
        testName: testName,
        line: line
    )
    #else
    assertSnapshot(
        of: view,
        as: .image(layout: .fixed(width: size.width, height: size.height)),
        named: name,
        file: file,
        testName: testName,
        line: line
    )
    #endif
}

private let snapshotPages = [
    OnboardingPage(
        id: "welcome",
        title: "Welcome to Myclok",
        subtitle: "Set up commute tracking and understand where your time goes.",
        systemImage: "clock.badge.checkmark",
        accentLabel: "Setup"
    ),
    OnboardingPage(
        id: "permissions",
        title: "Enable Location",
        subtitle: "Allow location access so your commute can start and stop automatically.",
        systemImage: "location.fill",
        accentLabel: "Privacy"
    ),
    OnboardingPage(
        id: "summary",
        title: "Review Trends",
        subtitle: "See weekly summaries that make your routine easier to compare.",
        systemImage: "chart.line.uptrend.xyaxis",
        accentLabel: "Insights"
    )
]

private let snapshotTheme = OnboardingTheme(
    background: LinearGradient(
        colors: [Color(hex: 0x07111F), Color(hex: 0x182A45)],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    ),
    cardColor: Color(hex: 0x111827),
    raisedColor: Color(hex: 0x233044),
    highlightColor: Color(hex: 0x2563EB, alpha: 0.22),
    primaryTextColor: Color(hex: 0xF8FAFC),
    secondaryTextColor: Color(hex: 0xCBD5E1),
    accentColor: Color(hex: 0x38BDF8),
    warningColor: Color(hex: 0xFBBF24),
    borderColor: Color.white.opacity(0.16),
    buttonGradient: LinearGradient(
        colors: [Color(hex: 0x0EA5E9), Color(hex: 0x2563EB)],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )
)

private extension Color {
    init(hex: UInt32, alpha: Double = 1.0) {
        let red = Double((hex >> 16) & 0xFF) / 255.0
        let green = Double((hex >> 8) & 0xFF) / 255.0
        let blue = Double(hex & 0xFF) / 255.0
        self.init(.sRGB, red: red, green: green, blue: blue, opacity: alpha)
    }
}
