# Contributing

Thanks for taking the time to improve Onboarding.

## Development Setup

1. Clone the repository.
2. Open the package in Xcode, or work from the command line.
3. Run the test suite before opening a pull request:

```sh
xcodebuild test -scheme Onboarding -destination 'platform=iOS Simulator,name=iPhone 17,OS=latest'
```

## Pull Request Guidelines

- Keep changes focused on one problem or feature.
- Add or update tests for behavior changes.
- Update `README.md` when public API or usage changes.
- Update `CHANGELOG.md` for user-visible changes.
- Keep the package iOS-focused unless platform support is intentionally expanded.
- Avoid adding production dependencies unless there is a strong reason.

## Release Notes

This package follows Semantic Versioning:

- Patch releases fix bugs without changing public API.
- Minor releases add backwards-compatible functionality.
- Major releases can include breaking changes.

Document notable changes in `CHANGELOG.md`.
