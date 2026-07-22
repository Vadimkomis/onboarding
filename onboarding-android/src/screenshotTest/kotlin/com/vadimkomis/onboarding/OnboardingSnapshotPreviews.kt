package com.vadimkomis.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(
    name = "initial-screen",
    device = snapshotDevice,
    locale = snapshotLocale,
    fontScale = 1f,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
)
@Composable
private fun OnboardingFlowInitialScreenSnapshot() {
    OnboardingFlow(
        pages = snapshotPages,
        theme = snapshotTheme,
        onComplete = {},
    )
}

@PreviewTest
@Preview(
    name = "single-page-complete",
    device = snapshotDevice,
    locale = snapshotLocale,
    fontScale = 1f,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
)
@Composable
private fun OnboardingFlowSinglePageCompleteSnapshot() {
    OnboardingFlow(
        pages = listOf(snapshotPages.first()),
        theme = snapshotTheme,
        completeTitle = "Start Tracking",
        onComplete = {},
    )
}

@PreviewTest
@Preview(
    name = "completed-content",
    device = snapshotDevice,
    locale = snapshotLocale,
    fontScale = 1f,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
)
@Composable
private fun OnboardingGateCompletedContentSnapshot() {
    OnboardingGateState(
        isCompleted = true,
        pages = snapshotPages,
        theme = snapshotTheme,
        onComplete = {},
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Main App",
                color = Color.Black,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private const val snapshotDevice = "spec:width=390dp,height=844dp,dpi=480"
private const val snapshotLocale = "en-rUS"

private val snapshotIcon = ImageVector.Builder(
    name = "SnapshotSparkle",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(12f, 2f)
        lineTo(14.8f, 9.2f)
        lineTo(22f, 12f)
        lineTo(14.8f, 14.8f)
        lineTo(12f, 22f)
        lineTo(9.2f, 14.8f)
        lineTo(2f, 12f)
        lineTo(9.2f, 9.2f)
        close()
    }
}.build()

private val snapshotPages = listOf(
    OnboardingPage(
        id = "welcome",
        title = "Welcome to Myclok",
        subtitle = "Set up commute tracking and understand where your time goes.",
        media = OnboardingPageMedia.Icon(snapshotIcon, "Setup illustration"),
        accentLabel = "Setup",
    ),
    OnboardingPage(
        id = "permissions",
        title = "Enable Location",
        subtitle = "Allow location access so your commute can start and stop automatically.",
        media = OnboardingPageMedia.Icon(snapshotIcon, "Location illustration"),
        accentLabel = "Privacy",
    ),
    OnboardingPage(
        id = "summary",
        title = "Review Trends",
        subtitle = "See weekly summaries that make your routine easier to compare.",
        media = OnboardingPageMedia.Icon(snapshotIcon, "Trends illustration"),
        accentLabel = "Insights",
    ),
)

private val snapshotTheme = OnboardingTheme(
    background = Brush.linearGradient(
        colors = listOf(Color(0xFF07111F), Color(0xFF182A45)),
    ),
    cardColor = Color(0xFF111827),
    raisedColor = Color(0xFF233044),
    highlightColor = Color(0x382563EB),
    primaryTextColor = Color(0xFFF8FAFC),
    secondaryTextColor = Color(0xFFCBD5E1),
    accentColor = Color(0xFF38BDF8),
    warningColor = Color(0xFFFBBF24),
    borderColor = Color.White.copy(alpha = 0.16f),
    buttonGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0EA5E9), Color(0xFF2563EB)),
    ),
)
