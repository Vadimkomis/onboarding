package com.vadimkomis.onboarding

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OnboardingGateTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun completedKeyShowsHostContentImmediately() {
        val store = FakeCompletionStore(completedKeys = setOf("finished"))
        composeRule.setContent {
            OnboardingGate(
                storageKey = "finished",
                pages = listOf(testPage()),
                completionStore = store,
            ) {
                androidx.compose.material3.Text("Main content")
            }
        }

        composeRule.onNodeWithText("Main content").assertIsDisplayed()
        composeRule.onNodeWithTag(OnboardingTestTags.flow).assertDoesNotExist()
    }

    @Test
    fun completionPersistsKeyBeforeShowingHostContent() {
        val store = FakeCompletionStore()
        composeRule.setContent {
            OnboardingGate(
                storageKey = "first-run",
                pages = listOf(testPage()),
                completionStore = store,
            ) {
                androidx.compose.material3.Text("Main content")
            }
        }

        composeRule.onNodeWithText("Get Started").performClick()
        composeRule.onNodeWithTag(OnboardingTestTags.gateContent).assertIsDisplayed()
        composeRule.runOnIdle {
            assertTrue(store.isCompleted("first-run"))
            assertEquals(listOf("first-run"), store.markedKeys)
        }
    }

    @Test
    fun changingStorageKeyReadsTheNewKey() {
        val store = FakeCompletionStore(completedKeys = setOf("second"))
        var storageKey by mutableStateOf("first")
        composeRule.setContent {
            OnboardingGate(
                storageKey = storageKey,
                pages = listOf(testPage()),
                completionStore = store,
            ) {
                androidx.compose.material3.Text("Main content")
            }
        }

        composeRule.onNodeWithTag(OnboardingTestTags.flow).assertIsDisplayed()
        composeRule.runOnIdle { storageKey = "second" }
        composeRule.onNodeWithText("Main content").assertIsDisplayed()
    }

    @Test
    fun defaultStorePersistsCompletionUnderTheStorageKey() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferences = context.getSharedPreferences(
            onboardingPreferencesName,
            Context.MODE_PRIVATE,
        )
        val storageKey = "instrumentation.default-store.completed"
        preferences.edit().remove(storageKey).commit()

        try {
            composeRule.setContent {
                OnboardingGate(storageKey = storageKey, pages = listOf(testPage())) {
                    androidx.compose.material3.Text("Main content")
                }
            }

            composeRule.onNodeWithText("Get Started").performClick()
            composeRule.onNodeWithText("Main content").assertIsDisplayed()
            composeRule.runOnIdle { assertTrue(preferences.getBoolean(storageKey, false)) }
        } finally {
            preferences.edit().remove(storageKey).commit()
        }
    }
}

private class FakeCompletionStore(
    completedKeys: Set<String> = emptySet(),
) : OnboardingCompletionStore {
    private val completed = completedKeys.toMutableSet()
    val markedKeys = mutableListOf<String>()

    override fun isCompleted(storageKey: String): Boolean = storageKey in completed

    override fun markCompleted(storageKey: String) {
        completed += storageKey
        markedKeys += storageKey
    }
}

private fun testPage(): OnboardingPage = OnboardingPage(
    id = "welcome",
    title = "Welcome",
    subtitle = "Start here.",
    media = OnboardingPageMedia.Drawable(android.R.drawable.ic_dialog_info, "Welcome image"),
    accentLabel = "New",
)
