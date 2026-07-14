package com.vadimkomis.onboarding

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingFlowLayoutTest {
    @Test
    fun mediaUsesPortraitBoundsOnARegularPhone() {
        val size = onboardingMediaSize(390.dp, 600.dp)

        assertEquals(330.dp, size.height)
        assertTrue(size.width < size.height)
        assertTrue(size.width <= 320.dp)
    }

    @Test
    fun mediaNeverExceedsACompactViewport() {
        val size = onboardingMediaSize(240.dp, 100.dp)

        assertEquals(100.dp, size.height)
        assertTrue(size.width <= 208.dp)
        assertTrue(size.width >= 0.dp)
    }

    @Test
    fun mediaWidthNeverBecomesNegative() {
        val size = onboardingMediaSize(20.dp, 500.dp)

        assertEquals(0.dp, size.width)
    }
}
