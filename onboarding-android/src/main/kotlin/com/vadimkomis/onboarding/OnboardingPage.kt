package com.vadimkomis.onboarding

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
public data class OnboardingPage(
    public val id: String,
    public val title: String,
    public val subtitle: String,
    public val media: OnboardingPageMedia,
    public val accentLabel: String,
)

@Immutable
public sealed interface OnboardingPageMedia {
    public val contentDescription: String?

    public data class Icon(
        public val imageVector: ImageVector,
        public override val contentDescription: String? = null,
    ) : OnboardingPageMedia

    public data class Drawable(
        @param:DrawableRes public val resourceId: Int,
        public override val contentDescription: String? = null,
    ) : OnboardingPageMedia

    public data class Video(
        public val uri: Uri,
        public override val contentDescription: String? = null,
    ) : OnboardingPageMedia
}
