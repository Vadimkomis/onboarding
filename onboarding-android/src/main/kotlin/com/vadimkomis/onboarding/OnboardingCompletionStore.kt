package com.vadimkomis.onboarding

import android.annotation.SuppressLint
import android.content.SharedPreferences

/**
 * Thread-safe completion storage for [OnboardingGate].
 *
 * Reads can run on a background thread. Writes run from the UI thread and must stage persistence
 * without blocking on disk or network I/O, as [SharedPreferences.Editor.apply] does.
 */
public interface OnboardingCompletionStore {
    public fun isCompleted(storageKey: String): Boolean

    public fun markCompleted(storageKey: String)
}

public class SharedPreferencesOnboardingCompletionStore(
    private val sharedPreferences: SharedPreferences,
) : OnboardingCompletionStore {
    override fun isCompleted(storageKey: String): Boolean =
        sharedPreferences.getBoolean(storageKey, false)

    // This adapter intentionally depends on the platform interface instead of requiring core-ktx.
    @SuppressLint("UseKtx")
    override fun markCompleted(storageKey: String) {
        sharedPreferences.edit()
            .putBoolean(storageKey, true)
            .apply()
    }
}
