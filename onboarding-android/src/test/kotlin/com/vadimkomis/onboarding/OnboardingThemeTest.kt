package com.vadimkomis.onboarding

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class OnboardingThemeTest {
    @Test
    fun customThemeStoresConfiguration() {
        val background = Brush.linearGradient(listOf(Color.Black, Color.Blue))
        val buttonGradient = Brush.linearGradient(listOf(Color.Blue, Color.Magenta))
        val theme = OnboardingTheme(
            background = background,
            cardColor = Color.DarkGray,
            raisedColor = Color.Gray,
            highlightColor = Color.Blue.copy(alpha = 0.2f),
            primaryTextColor = Color.White,
            secondaryTextColor = Color.LightGray,
            accentColor = Color.Cyan,
            warningColor = Color.Yellow,
            borderColor = Color.White.copy(alpha = 0.1f),
            buttonGradient = buttonGradient,
        )

        assertEquals(background, theme.background)
        assertEquals(Color.DarkGray, theme.cardColor)
        assertEquals(Color.Gray, theme.raisedColor)
        assertEquals(Color.Blue.copy(alpha = 0.2f), theme.highlightColor)
        assertEquals(Color.White, theme.primaryTextColor)
        assertEquals(Color.LightGray, theme.secondaryTextColor)
        assertEquals(Color.Cyan, theme.accentColor)
        assertEquals(Color.Yellow, theme.warningColor)
        assertEquals(Color.White.copy(alpha = 0.1f), theme.borderColor)
        assertEquals(buttonGradient, theme.buttonGradient)
    }

    @Test
    fun standardThemeMatchesTheCrossPlatformPalette() {
        val theme = OnboardingTheme.standard

        assertNotNull(theme.background)
        assertEquals(Color(0xFF111827), theme.cardColor)
        assertEquals(Color(0xFF1F2937), theme.raisedColor)
        assertEquals(Color(0x331D4ED8), theme.highlightColor)
        assertEquals(Color(0xFFF9FAFB), theme.primaryTextColor)
        assertEquals(Color(0xFFAAB2C0), theme.secondaryTextColor)
        assertEquals(Color(0xFF2563EB), theme.accentColor)
        assertEquals(Color(0xFFF59E0B), theme.warningColor)
        assertEquals(Color.White.copy(alpha = 0.16f), theme.borderColor)
        assertNotNull(theme.buttonGradient)
    }
}
