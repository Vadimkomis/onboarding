package com.vadimkomis.onboarding

import android.content.SharedPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingCompletionStoreTest {
    @Test
    fun missingKeyIsNotCompleted() {
        val store = SharedPreferencesOnboardingCompletionStore(InMemorySharedPreferences())

        assertFalse(store.isCompleted("first-run"))
    }

    @Test
    fun markCompletedPersistsTheKey() {
        val store = SharedPreferencesOnboardingCompletionStore(InMemorySharedPreferences())

        store.markCompleted("first-run")

        assertTrue(store.isCompleted("first-run"))
    }

    @Test
    fun completionKeysRemainIsolated() {
        val store = SharedPreferencesOnboardingCompletionStore(InMemorySharedPreferences())

        store.markCompleted("version-one")

        assertTrue(store.isCompleted("version-one"))
        assertFalse(store.isCompleted("version-two"))
    }
}

private class InMemorySharedPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any?>()

    override fun getAll(): MutableMap<String, *> = values.toMutableMap()

    override fun getString(key: String?, defaultValue: String?): String? =
        values[key] as? String ?: defaultValue

    override fun getStringSet(key: String?, defaultValues: MutableSet<String>?): MutableSet<String>? =
        @Suppress("UNCHECKED_CAST")
        ((values[key] as? Set<String>)?.toMutableSet() ?: defaultValues)

    override fun getInt(key: String?, defaultValue: Int): Int =
        values[key] as? Int ?: defaultValue

    override fun getLong(key: String?, defaultValue: Long): Long =
        values[key] as? Long ?: defaultValue

    override fun getFloat(key: String?, defaultValue: Float): Float =
        values[key] as? Float ?: defaultValue

    override fun getBoolean(key: String?, defaultValue: Boolean): Boolean =
        values[key] as? Boolean ?: defaultValue

    override fun contains(key: String?): Boolean = values.containsKey(key)

    override fun edit(): SharedPreferences.Editor = Editor(values)

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = Unit

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = Unit
}

private class Editor(
    private val values: MutableMap<String, Any?>,
) : SharedPreferences.Editor {
    private val pendingValues = mutableMapOf<String, Any?>()
    private val removedKeys = mutableSetOf<String>()
    private var shouldClear = false

    override fun putString(key: String?, value: String?): SharedPreferences.Editor =
        put(key, value)

    override fun putStringSet(
        key: String?,
        values: MutableSet<String>?,
    ): SharedPreferences.Editor = put(key, values?.toSet())

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor = put(key, value)

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor = put(key, value)

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = put(key, value)

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = put(key, value)

    override fun remove(key: String?): SharedPreferences.Editor = apply {
        if (key != null) {
            removedKeys += key
        }
    }

    override fun clear(): SharedPreferences.Editor = apply {
        shouldClear = true
    }

    override fun commit(): Boolean {
        applyChanges()
        return true
    }

    override fun apply() {
        applyChanges()
    }

    private fun put(key: String?, value: Any?): SharedPreferences.Editor = apply {
        if (key != null) {
            pendingValues[key] = value
        }
    }

    private fun applyChanges() {
        if (shouldClear) {
            values.clear()
        }
        removedKeys.forEach(values::remove)
        values.putAll(pendingValues)
    }
}
