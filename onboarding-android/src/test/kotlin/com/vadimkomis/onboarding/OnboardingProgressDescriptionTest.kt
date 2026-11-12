package com.vadimkomis.onboarding

import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingProgressDescriptionTest {
    @Test
    fun defaultDescriptionNamesCurrentPageAndPageCount() {
        assertEquals("Page 2 of 3", defaultOnboardingProgressDescription(2, 3))
    }

    @Test
    fun defaultDescriptionExplainsAnEmptyFlow() {
        assertEquals("No onboarding pages", defaultOnboardingProgressDescription(0, 0))
    }
}
