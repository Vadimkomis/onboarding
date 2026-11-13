// swift-tools-version: 5.9

import PackageDescription

// Tests live in Tests/Package.swift so apps that add this package fetch no test-only dependencies.
let package = Package(
    name: "Onboarding",
    platforms: [
        .iOS(.v17)
    ],
    products: [
        .library(
            name: "Onboarding",
            targets: ["Onboarding"]
        )
    ],
    targets: [
        .target(name: "Onboarding")
    ]
)
