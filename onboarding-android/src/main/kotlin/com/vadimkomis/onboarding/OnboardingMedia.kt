package com.vadimkomis.onboarding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun OnboardingMedia(
    media: OnboardingPageMedia,
    theme: OnboardingTheme,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    playerFactory: OnboardingVideoPlayerFactory = media3VideoPlayerFactory,
    drawableLoader: OnboardingDrawableLoader = cachedOnboardingDrawableLoader,
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

            is OnboardingPageMedia.Drawable -> {
                val context = LocalContext.current
                val configuration = LocalConfiguration.current
                val environment = drawableEnvironmentKey(
                    context = context,
                    configuration = configuration,
                    resourceThemeVersion = media.resourceThemeVersion,
                )
                val request = remember(context, environment) {
                    createDrawableLoadRequest(
                        context = context,
                        configuration = environment.configuration,
                        resourceThemeVersion = media.resourceThemeVersion,
                    )
                }
                DrawableMedia(
                    resourceId = media.resourceId,
                    loader = drawableLoader,
                    request = request,
                    modifier = Modifier.fillMaxSize(),
                )
            }

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
private fun DrawableMedia(
    resourceId: Int,
    loader: OnboardingDrawableLoader,
    request: DrawableLoadRequest,
    modifier: Modifier,
) {
    val drawable by produceState<Drawable?>(null, resourceId, request, loader) {
        value = null
        value = withContext(Dispatchers.IO) {
            loadOnboardingDrawableSafely(loader, request, resourceId)
        }
    }
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
        },
        update = { imageView ->
            if (imageView.drawable !== drawable) {
                imageView.setImageDrawable(drawable)
            }
        },
        modifier = modifier,
    )
}
