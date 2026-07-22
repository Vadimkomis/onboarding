package com.vadimkomis.onboarding

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OnboardingPageTest {
    @Test
    fun pageStoresIconConfiguration() {
        val icon = ImageVector.Builder(
            name = "sparkles",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).build()
        val media = OnboardingPageMedia.Icon(
            imageVector = icon,
            contentDescription = "Welcome",
        )

        val page = OnboardingPage(
            id = "welcome",
            title = "Welcome",
            subtitle = "Start here.",
            media = media,
            accentLabel = "New",
        )

        assertEquals("welcome", page.id)
        assertEquals("Welcome", page.title)
        assertEquals("Start here.", page.subtitle)
        assertEquals(media, page.media)
        assertEquals("New", page.accentLabel)
    }

    @Test
    fun drawableStoresResourceAndOptionalDescription() {
        val media = OnboardingPageMedia.Drawable(resourceId = 42)

        assertEquals(42, media.resourceId)
        assertNull(media.contentDescription)
        assertEquals(0, media.resourceThemeVersion)
    }

    @Test
    fun drawableStoresExplicitResourceThemeVersion() {
        val media = OnboardingPageMedia.Drawable(
            resourceId = 42,
            resourceThemeVersion = 3,
        )

        assertEquals(3, media.resourceThemeVersion)
    }
}
