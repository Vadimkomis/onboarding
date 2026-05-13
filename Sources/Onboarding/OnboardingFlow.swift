import AVKit
import SwiftUI
import UIKit

public struct OnboardingFlow: View {
    private let pages: [OnboardingPage]
    private let theme: OnboardingTheme
    private let continueTitle: String
    private let completeTitle: String
    private let skipTitle: String
    private let allowsSkipping: Bool
    private let onComplete: () -> Void

    @State private var state: OnboardingFlowState

    public init(
        pages: [OnboardingPage],
        theme: OnboardingTheme = .standard,
        continueTitle: String = "Continue",
        completeTitle: String = "Get Started",
        skipTitle: String = "Skip",
        allowsSkipping: Bool = true,
        onComplete: @escaping () -> Void
    ) {
        self.pages = pages
        self.theme = theme
        self.continueTitle = continueTitle
        self.completeTitle = completeTitle
        self.skipTitle = skipTitle
        self.allowsSkipping = allowsSkipping
        self.onComplete = onComplete
        _state = State(initialValue: OnboardingFlowState(pageCount: pages.count))
    }

    public var body: some View {
        ZStack {
            theme.background
                .ignoresSafeArea()

            VStack(spacing: 20) {
                progressIndicator

                pageContent

                controls
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 18)
        }
    }

    @ViewBuilder
    private var pageContent: some View {
        #if os(iOS)
        TabView(selection: $state.selectedIndex) {
            ForEach(Array(pages.enumerated()), id: \.element.id) { index, page in
                OnboardingPageView(
                    page: page,
                    theme: theme,
                    isActive: index == state.selectedIndex
                )
                    .tag(index)
            }
        }
        .tabViewStyle(.page(indexDisplayMode: .never))
        #else
        if pages.indices.contains(state.selectedIndex) {
            OnboardingPageView(
                page: pages[state.selectedIndex],
                theme: theme,
                isActive: true
            )
        } else {
            Spacer()
        }
        #endif
    }

    private var progressIndicator: some View {
        HStack(spacing: 8) {
            ForEach(pages.indices, id: \.self) { index in
                Capsule()
                    .fill(index == state.selectedIndex ? theme.accentColor : theme.raisedColor)
                    .frame(width: index == state.selectedIndex ? 30 : 8, height: 8)
                    .animation(.easeInOut(duration: 0.2), value: state.selectedIndex)
            }
        }
        .frame(maxWidth: .infinity, alignment: .center)
        .accessibilityHidden(true)
    }

    private var controls: some View {
        VStack(spacing: 12) {
            Button(action: advance) {
                Text(isLastPage ? completeTitle : continueTitle)
            }
            .buttonStyle(OnboardingPrimaryButtonStyle(theme: theme))

            if allowsSkipping {
                Button(action: onComplete) {
                    Text(skipTitle)
                        .font(.subheadline.weight(.semibold))
                        .foregroundColor(theme.secondaryTextColor)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                }
                .opacity(isLastPage ? 0 : 1)
                .disabled(isLastPage)
            }
        }
    }

    private var isLastPage: Bool {
        state.isLastPage
    }

    private func advance() {
        guard !state.isLastPage else {
            onComplete()
            return
        }

        withAnimation(.easeInOut(duration: 0.24)) {
            _ = state.advance()
        }
    }
}

private struct OnboardingPageView: View {
    let page: OnboardingPage
    let theme: OnboardingTheme
    let isActive: Bool

    var body: some View {
        GeometryReader { proxy in
            let mediaSize = OnboardingMediaLayout.size(for: proxy.size)

            VStack(spacing: 28) {
                Spacer(minLength: 12)

                VStack(spacing: 18) {
                    OnboardingMediaView(
                        media: page.media,
                        theme: theme,
                        isActive: isActive,
                        mediaSize: mediaSize
                    )

                    Text(page.accentLabel.uppercased())
                        .font(.caption.weight(.bold))
                        .foregroundColor(theme.warningColor)
                }

                VStack(spacing: 12) {
                    Text(page.title)
                        .font(.largeTitle.weight(.bold))
                        .foregroundColor(theme.primaryTextColor)
                        .multilineTextAlignment(.center)
                        .minimumScaleFactor(0.82)

                    Text(page.subtitle)
                        .font(.body)
                        .foregroundColor(theme.secondaryTextColor)
                        .multilineTextAlignment(.center)
                        .lineSpacing(3)
                        .frame(maxWidth: 330)
                }
                .layoutPriority(1)

                Spacer(minLength: 12)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct OnboardingMediaView: View {
    let media: OnboardingPageMedia
    let theme: OnboardingTheme
    let isActive: Bool
    let mediaSize: CGSize

    var body: some View {
        switch media {
        case let .systemImage(systemImage):
            Image(systemName: systemImage)
                .font(.system(size: 64, weight: .semibold))
                .foregroundColor(theme.accentColor)

        case let .image(name):
            Image(name)
                .resizable()
                .scaledToFill()
                .frame(width: mediaSize.width, height: mediaSize.height)
                .clipped()

        case let .video(url):
            OnboardingAutoplayVideoView(url: url, isActive: isActive)
                .frame(width: mediaSize.width, height: mediaSize.height)
                .clipped()
        }
    }
}

private enum OnboardingMediaLayout {
    static func size(for availableSize: CGSize) -> CGSize {
        let width = min(320, max(248, availableSize.width - 32))
        let reservedTextHeight: CGFloat = 250
        let height = min(540, max(220, availableSize.height - reservedTextHeight))

        return CGSize(width: width, height: height)
    }
}

private struct OnboardingAutoplayVideoView: View {
    let url: URL
    let isActive: Bool

    @State private var player: AVPlayer?

    var body: some View {
        OnboardingAspectFillVideoPlayer(player: player)
            .onAppear(perform: updatePlayback)
            .onChange(of: isActive) { updatePlayback() }
            .onDisappear(perform: stop)
            .onReceive(NotificationCenter.default.publisher(for: .AVPlayerItemDidPlayToEndTime)) { notification in
                guard notification.object as? AVPlayerItem == player?.currentItem else {
                    return
                }

                player?.seek(to: .zero)
                player?.play()
            }
    }

    private func updatePlayback() {
        if isActive {
            play()
        } else {
            stop()
        }
    }

    private func play() {
        if player == nil {
            let player = AVPlayer(url: url)
            player.isMuted = true
            self.player = player
        }

        player?.play()
    }

    private func stop() {
        player?.pause()
        player = nil
    }
}

private struct OnboardingAspectFillVideoPlayer: UIViewRepresentable {
    let player: AVPlayer?

    func makeUIView(context: Context) -> OnboardingPlayerView {
        let view = OnboardingPlayerView()
        view.videoGravity = .resizeAspectFill
        view.player = player
        return view
    }

    func updateUIView(_ uiView: OnboardingPlayerView, context: Context) {
        uiView.videoGravity = .resizeAspectFill
        uiView.player = player
    }
}

private final class OnboardingPlayerView: UIView {
    override static var layerClass: AnyClass {
        AVPlayerLayer.self
    }

    var playerLayer: AVPlayerLayer {
        guard let playerLayer = layer as? AVPlayerLayer else {
            preconditionFailure("OnboardingPlayerView must be backed by AVPlayerLayer")
        }

        return playerLayer
    }

    var player: AVPlayer? {
        get { playerLayer.player }
        set { playerLayer.player = newValue }
    }

    var videoGravity: AVLayerVideoGravity {
        get { playerLayer.videoGravity }
        set { playerLayer.videoGravity = newValue }
    }
}

private struct OnboardingPrimaryButtonStyle: ButtonStyle {
    let theme: OnboardingTheme

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.headline.weight(.semibold))
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
            .background(theme.buttonGradient)
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .stroke(theme.borderColor, lineWidth: 1)
            )
            .opacity(configuration.isPressed ? 0.88 : 1)
            .scaleEffect(configuration.isPressed ? 0.99 : 1)
    }
}
