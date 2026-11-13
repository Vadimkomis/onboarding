// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "OnboardingTests",
    platforms: [
        .iOS(.v17)
    ],
    dependencies: [
        .package(name: "Onboarding", path: ".."),
        .package(
            url: "https://github.com/pointfreeco/swift-snapshot-testing",
            from: "1.19.2"
        )
    ],
    targets: [
        .testTarget(
            name: "OnboardingTests",
            dependencies: [
                .product(name: "Onboarding", package: "Onboarding"),
                .product(name: "SnapshotTesting", package: "swift-snapshot-testing")
            ],
            path: "OnboardingTests",
            exclude: ["__Snapshots__"]
        )
    ]
)
