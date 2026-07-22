package com.vadimkomis.onboarding

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal const val onboardingPreferencesName: String = "com.vadimkomis.onboarding.preferences"

@Composable
public fun OnboardingGate(
    storageKey: String,
    pages: List<OnboardingPage>,
    modifier: Modifier = Modifier,
    theme: OnboardingTheme = OnboardingTheme.standard,
    allowsSkipping: Boolean = true,
    completionStore: OnboardingCompletionStore? = null,
    content: @Composable () -> Unit,
) {
    val store = completionStore ?: rememberDefaultCompletionStore()
    var isCompleted by remember(storageKey, store) { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(storageKey, store) {
        isCompleted = withContext(Dispatchers.IO) {
            store.isCompleted(storageKey)
        }
    }

    OnboardingGateState(
        isCompleted = isCompleted,
        pages = pages,
        modifier = modifier,
        theme = theme,
        allowsSkipping = allowsSkipping,
        onComplete = {
            store.markCompleted(storageKey)
            isCompleted = true
        },
        content = content,
    )
}

@Composable
internal fun OnboardingGateState(
    isCompleted: Boolean?,
    pages: List<OnboardingPage>,
    modifier: Modifier = Modifier,
    theme: OnboardingTheme = OnboardingTheme.standard,
    allowsSkipping: Boolean = true,
    onComplete: () -> Unit,
    content: @Composable () -> Unit,
) {
    when (isCompleted) {
        true -> Box(modifier = modifier.testTag(OnboardingTestTags.gateContent)) {
            content()
        }

        false -> OnboardingFlow(
            pages = pages,
            modifier = modifier,
            theme = theme,
            allowsSkipping = allowsSkipping,
            onComplete = onComplete,
        )

        null -> Box(modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun rememberDefaultCompletionStore(): OnboardingCompletionStore {
    val applicationContext = LocalContext.current.applicationContext
    return remember(applicationContext) {
        val preferences = applicationContext.getSharedPreferences(
            onboardingPreferencesName,
            Context.MODE_PRIVATE,
        )
        SharedPreferencesOnboardingCompletionStore(preferences)
    }
}
