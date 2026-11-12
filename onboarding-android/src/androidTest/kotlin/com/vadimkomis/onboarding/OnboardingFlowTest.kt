package com.vadimkomis.onboarding

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.text.TextLayoutResult
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OnboardingFlowTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun primaryActionAdvancesThenCompletesOnlyOnce() {
        var completionCount = 0
        composeRule.setContent {
            OnboardingFlow(
                pages = testPages(),
                continueTitle = "Next",
                completeTitle = "Done",
                onComplete = { completionCount += 1 },
            )
        }

        composeRule.onNodeWithText("Next").assertHasClickAction().performClick()
        composeRule.onNodeWithText("Done").assertIsDisplayed().performClick().performClick()

        composeRule.runOnIdle { assertEquals(1, completionCount) }
    }

    @Test
    fun skipUsesCustomTitleAndCompletesOnlyOnce() {
        var completionCount = 0
        composeRule.setContent {
            OnboardingFlow(
                pages = testPages(),
                skipTitle = "Maybe later",
                onComplete = { completionCount += 1 },
            )
        }

        composeRule.onNodeWithText("Maybe later").assertHasClickAction().performClick().performClick()
        composeRule.runOnIdle { assertEquals(1, completionCount) }
    }

    @Test
    fun skipIsAbsentOnFinalPage() {
        composeRule.setContent {
            OnboardingFlow(pages = testPages(), onComplete = {})
        }

        composeRule.onNodeWithTag(OnboardingTestTags.primaryButton).performClick()
        composeRule.onNodeWithTag(OnboardingTestTags.skipButton).assertDoesNotExist()
    }

    @Test
    fun skipIsAbsentWhenSkippingIsDisabled() {
        composeRule.setContent {
            OnboardingFlow(
                pages = testPages(),
                allowsSkipping = false,
                onComplete = {},
            )
        }

        composeRule.onNodeWithTag(OnboardingTestTags.skipButton).assertDoesNotExist()
    }

    @Test
    fun pagerSupportsTouchNavigationAndUpdatesProgressDescription() {
        composeRule.setContent {
            OnboardingFlow(pages = testPages(), onComplete = {})
        }

        composeRule.onNodeWithTag(OnboardingTestTags.pager).performTouchInput { swipeLeft() }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Get Started").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNode(hasContentDescription("Page 2 of 2")).assertExists()
    }

    @Test
    fun progressAnnouncesAppSuppliedDescription() {
        composeRule.setContent {
            OnboardingFlow(
                pages = testPages(),
                progressDescription = { page, pageCount -> "Seite $page von $pageCount" },
                onComplete = {},
            )
        }

        composeRule.onNodeWithTag(OnboardingTestTags.progress)
            .assertContentDescriptionEquals("Seite 1 von 2")
    }

    @Test
    fun emptyFlowAsksTheAppToDescribeZeroPages() {
        composeRule.setContent {
            OnboardingFlow(
                pages = emptyList(),
                progressDescription = { page, pageCount -> "$page/$pageCount" },
                onComplete = {},
            )
        }

        composeRule.onNodeWithTag(OnboardingTestTags.progress)
            .assertContentDescriptionEquals("0/0")
    }

    @Test
    fun primaryButtonLabelUsesThemeButtonTextColor() {
        composeRule.setContent {
            OnboardingFlow(
                pages = testPages(),
                theme = OnboardingTheme.standard.copy(buttonTextColor = Color.Black),
                onComplete = {},
            )
        }

        assertEquals(Color.Black, textColor("Continue"))
    }

    @Test
    fun primaryButtonLabelIsWhiteInStandardTheme() {
        composeRule.setContent {
            OnboardingFlow(pages = testPages(), onComplete = {})
        }

        assertEquals(Color.White, textColor("Continue"))
    }

    private fun textColor(text: String): Color {
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single().layoutInput.style.color
    }

    @Test
    fun emptyFlowOffersCompletionWithoutSkip() {
        var completionCount = 0
        composeRule.setContent {
            OnboardingFlow(pages = emptyList(), onComplete = { completionCount += 1 })
        }

        composeRule.onNodeWithText("Get Started").performClick()
        composeRule.onNodeWithTag(OnboardingTestTags.skipButton).assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(1, completionCount) }
    }

    @Test
    fun selectedPageSurvivesSavedStateRestoration() {
        val restorationTester = StateRestorationTester(composeRule)
        restorationTester.setContent {
            OnboardingFlow(pages = testPages(), onComplete = {})
        }

        composeRule.onNodeWithTag(OnboardingTestTags.primaryButton).performClick()
        composeRule.onNodeWithText("Get Started").assertIsDisplayed()
        restorationTester.emulateSavedInstanceStateRestore()

        composeRule.onNodeWithText("Get Started").assertIsDisplayed()
        composeRule.onNode(hasContentDescription("Page 2 of 2")).assertExists()
    }
}

private fun testPages(): List<OnboardingPage> = listOf(
    OnboardingPage(
        id = "welcome",
        title = "Welcome",
        subtitle = "Start here.",
        media = OnboardingPageMedia.Drawable(android.R.drawable.ic_dialog_info, "Welcome image"),
        accentLabel = "New",
    ),
    OnboardingPage(
        id = "ready",
        title = "Ready",
        subtitle = "You are ready to begin.",
        media = OnboardingPageMedia.Drawable(android.R.drawable.ic_dialog_info, "Ready image"),
        accentLabel = "Go",
    ),
)
