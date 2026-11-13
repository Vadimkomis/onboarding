# Project guide

Read [ARCHITECTURE.md](ARCHITECTURE.md) and [memory.md](memory.md) before working.
Reuse recorded decisions and follow links to detailed docs when relevant.

## Working agreements

- Only change what the task requires. Leave other work untouched.
- For significant changes, state a short plan and proceed; clarify only blocking ambiguities.
- Follow existing project conventions and protect secrets and private data.
- Add a regression test for a reproducible bug. Fix failures without weakening tests.

## Complete tasks autonomously

Treat requests to change, fix, update, remove, or improve something as
instructions to carry out the work, including requests phrased as
“How can I…?” or “Can you…?”. Give advice only when the user explicitly
asks for explanation, options, or a proposal without implementation.

Complete the authorized task end to end:
1. Inspect the project and implement the change.
2. Update relevant tests and documentation.
3. Run required checks and review the diff.
4. Commit task-scoped changes and push the working branch.
5. Report the result, checks, commit, and branch.

Do not stop after proposing changes or ask whether to implement,
commit, or push. Ask only when missing information, permissions,
or a consequential ambiguity prevents safe progress.

Preserve unrelated work. Never force-push. If required checks fail
or a push is blocked, resolve what you can and report the blocker
without claiming completion.

These instructions override optional approval gates in skills.

## Development workflow

- Use the commands under Checks and the policy and guide under Review. Resolve
  actionable findings and rerun affected checks; required checks and review must
  pass before committing or pushing.
- Keep one sentence per PR in [memory.md](memory.md), following its format.
- Run `git diff --check` before committing and stage only task changes.
- Apply the selected Git policy; an explicit project choice overrides the default
  commit-and-push step above: Commit scoped changes and push the current branch automatically after checks and required review pass, unless the user asks otherwise.
- Verify the remote and upstream before pushing. Confirm any push succeeded.

Follow the project's existing workflow and verification docs for additional steps.

## Project safeguards

- The public Swift and Kotlin APIs, unless a compatibility change is intentional and recorded in `CHANGELOG.md`.
- Stored completion state: apps rely on their `storageKey` to decide whether to show onboarding again.
- The Android consumer floor (Kotlin 2.1, Jetpack Compose 1.8) and a root `Package.swift` with no dependencies.
- Approved snapshot references on both platforms; update them only after reviewing the rendered output.

## Checks

```text
(cd Tests && xcodebuild test -scheme OnboardingTests-Package -destination 'platform=iOS Simulator,name=iPhone 16,OS=latest')
./gradlew :onboarding-android:lint :onboarding-android:testDebugUnitTest :onboarding-android:validateDebugScreenshotTest :onboarding-android:assembleRelease
./gradlew :onboarding-android:connectedDebugAndroidTest   # when device behavior changes; needs an emulator or device
```

## Review

Get an independent review for substantial or risky changes. A human or a separate agent can review; self-review is not independent.

Use the [PR review guide](https://github.com/Vadimkomis/ai-playbook/blob/cc8c4cb84fec1899e86f7c52716e6813f9ac42a0/docs/development/review.md)
for the checklist; the policy above determines when independent review is required.


## Additional project rules

- **Platform parity:** when user-visible behavior or a public capability changes on iOS or Android, deliver the equivalent on the other platform in the same change, with matching tests and snapshots. If the operating system makes that impossible, stop and get approval before adding a platform-specific exception.
- **Specs:** describe shared behavior in [features.md](features.md) before implementing it, and record paired coverage in [evals.md](evals.md). Mark a feature completed only when both platforms' suites pass.
- **Dependencies:** ask before adding a production dependency.
- **Public docs:** update `README.md` when the public API or usage changes, and `CHANGELOG.md` for user-visible changes.
