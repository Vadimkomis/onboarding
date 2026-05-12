import SwiftUI

public struct OnboardingTheme {
    public let background: LinearGradient
    public let cardColor: Color
    public let raisedColor: Color
    public let highlightColor: Color
    public let primaryTextColor: Color
    public let secondaryTextColor: Color
    public let accentColor: Color
    public let warningColor: Color
    public let borderColor: Color
    public let buttonGradient: LinearGradient

    public init(
        background: LinearGradient,
        cardColor: Color,
        raisedColor: Color,
        highlightColor: Color,
        primaryTextColor: Color,
        secondaryTextColor: Color,
        accentColor: Color,
        warningColor: Color,
        borderColor: Color,
        buttonGradient: LinearGradient
    ) {
        self.background = background
        self.cardColor = cardColor
        self.raisedColor = raisedColor
        self.highlightColor = highlightColor
        self.primaryTextColor = primaryTextColor
        self.secondaryTextColor = secondaryTextColor
        self.accentColor = accentColor
        self.warningColor = warningColor
        self.borderColor = borderColor
        self.buttonGradient = buttonGradient
    }

    public static let standard = OnboardingTheme(
        background: LinearGradient(
            colors: [Color(hex: 0x05070D), Color(hex: 0x101B2D)],
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        ),
        cardColor: Color(hex: 0x111827),
        raisedColor: Color(hex: 0x1F2937),
        highlightColor: Color(hex: 0x1D4ED8, alpha: 0.2),
        primaryTextColor: Color(hex: 0xF9FAFB),
        secondaryTextColor: Color(hex: 0xAAB2C0),
        accentColor: Color(hex: 0x2563EB),
        warningColor: Color(hex: 0xF59E0B),
        borderColor: Color.white.opacity(0.16),
        buttonGradient: LinearGradient(
            colors: [Color(hex: 0x2563EB), Color(hex: 0x1D4ED8)],
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        )
    )
}

private extension Color {
    init(hex: UInt32, alpha: Double = 1.0) {
        let red = Double((hex >> 16) & 0xFF) / 255.0
        let green = Double((hex >> 8) & 0xFF) / 255.0
        let blue = Double(hex & 0xFF) / 255.0
        self.init(.sRGB, red: red, green: green, blue: blue, opacity: alpha)
    }
}
