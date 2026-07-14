package com.vadimkomis.onboarding

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

private const val mediaLogTag = "OnboardingMedia"

@Composable
internal fun OnboardingMedia(
    media: OnboardingPageMedia,
    theme: OnboardingTheme,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    playerFactory: OnboardingVideoPlayerFactory = media3VideoPlayerFactory,
) {
    Box(
        modifier = modifier.semantics {
            media.contentDescription?.let { contentDescription = it }
        },
        contentAlignment = Alignment.Center,
    ) {
        when (media) {
            is OnboardingPageMedia.Icon -> Icon(
                imageVector = media.imageVector,
                contentDescription = null,
                tint = theme.accentColor,
                modifier = Modifier.size(64.dp),
            )

            is OnboardingPageMedia.Drawable -> DrawableMedia(
                resourceId = media.resourceId,
                modifier = Modifier.fillMaxSize(),
            )

            is OnboardingPageMedia.Video -> OnboardingVideo(
                uri = media.uri,
                isActive = isActive,
                playerFactory = playerFactory,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun DrawableMedia(resourceId: Int, modifier: Modifier) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
        },
        update = { imageView ->
            if (imageView.tag != resourceId) {
                imageView.tag = resourceId
                try {
                    imageView.setImageResource(resourceId)
                } catch (error: Resources.NotFoundException) {
                    imageView.setImageDrawable(null)
                    Log.w(mediaLogTag, "Unable to display drawable resource", error)
                }
            }
        },
        modifier = modifier,
    )
}
