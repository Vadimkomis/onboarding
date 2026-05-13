import Foundation

public enum OnboardingPageMedia: Equatable, Sendable {
    case systemImage(String)
    case image(name: String)
    case video(url: URL)
}

public struct OnboardingPage: Identifiable, Equatable, Sendable {
    public let id: String
    public let title: String
    public let subtitle: String
    public let media: OnboardingPageMedia
    public let accentLabel: String

    public var systemImage: String {
        guard case let .systemImage(systemImage) = media else {
            return ""
        }

        return systemImage
    }

    public init(
        id: String,
        title: String,
        subtitle: String,
        media: OnboardingPageMedia,
        accentLabel: String
    ) {
        self.id = id
        self.title = title
        self.subtitle = subtitle
        self.media = media
        self.accentLabel = accentLabel
    }

    public init(
        id: String,
        title: String,
        subtitle: String,
        systemImage: String,
        accentLabel: String
    ) {
        self.init(
            id: id,
            title: title,
            subtitle: subtitle,
            media: .systemImage(systemImage),
            accentLabel: accentLabel
        )
    }
}
