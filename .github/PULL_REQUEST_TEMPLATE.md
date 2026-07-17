## Summary

- 

## Platform Parity

- iOS implementation and coverage:
- Android implementation and coverage:
- Native constraints or differences:

## Testing

- [ ] `xcodebuild test -scheme Onboarding -destination 'platform=iOS Simulator,name=iPhone 16,OS=latest'`
- [ ] `./gradlew :onboarding-android:lint :onboarding-android:testDebugUnitTest :onboarding-android:validateDebugScreenshotTest :onboarding-android:assembleRelease`
- [ ] `./gradlew :onboarding-android:connectedDebugAndroidTest` when device behavior changes

## Checklist

- [ ] Implemented equivalent shared behavior on iOS and Android
- [ ] Added equivalent tests for both platforms, including paired snapshots for visual changes
- [ ] Updated `features.md` and `evals.md`
- [ ] Updated README or CHANGELOG when public usage changes
- [ ] Kept the change focused and backwards-compatible unless documented otherwise
