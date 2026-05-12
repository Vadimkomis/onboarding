import Foundation

public struct OnboardingPage: Identifiable, Equatable, Sendable {
    public let id: String
    public let title: String
    public let subtitle: String
    public let systemImage: String
    public let accentLabel: String

    public init(
        id: String,
        title: String,
        subtitle: String,
        systemImage: String,
        accentLabel: String
    ) {
        self.id = id
        self.title = title
        self.subtitle = subtitle
        self.systemImage = systemImage
        self.accentLabel = accentLabel
    }
}
