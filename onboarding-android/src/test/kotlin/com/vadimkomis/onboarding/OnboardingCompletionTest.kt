package com.vadimkomis.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class OnboardingCompletionTest {
    @Test
    fun failedCompletionCanBeRetried() {
        var completionAttempts = 0
        var wasInvoked = false

        try {
            wasInvoked = invokeCompletionOnce(wasInvoked) {
                completionAttempts += 1
                throw TestCompletionError
            }
            fail("Expected completion to fail")
        } catch (_: TestCompletionError) {
            assertFalse(wasInvoked)
        }

        wasInvoked = invokeCompletionOnce(wasInvoked) { completionAttempts += 1 }

        assertTrue(wasInvoked)
        assertEquals(2, completionAttempts)
    }

    @Test
    fun successfulCompletionOnlyRunsOnce() {
        var completionAttempts = 0
        var wasInvoked = invokeCompletionOnce(false) { completionAttempts += 1 }

        wasInvoked = invokeCompletionOnce(wasInvoked) { completionAttempts += 1 }

        assertTrue(wasInvoked)
        assertEquals(1, completionAttempts)
    }
}

private object TestCompletionError : RuntimeException()
