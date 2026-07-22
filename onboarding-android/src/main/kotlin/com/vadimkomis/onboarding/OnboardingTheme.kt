package com.vadimkomis.onboarding

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
public data class OnboardingTheme(
    public val background: Brush,
    public val cardColor: Color,
    public val raisedColor: Color,
    public val highlightColor: Color,
    public val primaryTextColor: Color,
    public val secondaryTextColor: Color,
    public val accentColor: Color,
    public val warningColor: Color,
    public val borderColor: Color,
    public val buttonGradient: Brush,
) {
    public companion object {
        public val standard: OnboardingTheme = OnboardingTheme(
            background = Brush.linearGradient(
                colors = listOf(Color(0xFF05070D), Color(0xFF101B2D)),
            ),
            cardColor = Color(0xFF111827),
            raisedColor = Color(0xFF1F2937),
            highlightColor = Color(0x331D4ED8),
            primaryTextColor = Color(0xFFF9FAFB),
            secondaryTextColor = Color(0xFFAAB2C0),
            accentColor = Color(0xFF2563EB),
            warningColor = Color(0xFFF59E0B),
            borderColor = Color.White.copy(alpha = 0.16f),
            buttonGradient = Brush.linearGradient(
                colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
            ),
        )
    }
}
