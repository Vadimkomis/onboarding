package com.vadimkomis.onboarding

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal typealias OnboardingDrawablePreloader =
    suspend (Context, Configuration, List<OnboardingPageMedia>) -> Unit
internal typealias OnboardingVideoPreloader = suspend (Context, List<OnboardingPageMedia>) -> Unit

@Composable
internal fun PreloadOnboardingMedia(
    media: List<OnboardingPageMedia>,
    drawablePreloader: OnboardingDrawablePreloader = ::preloadDrawables,
    videoPosterPreloader: OnboardingVideoPreloader = ::preloadVideoPosters,
) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val configuration = Configuration(LocalConfiguration.current)
    val environment = drawableEnvironmentKey(context, configuration, resourceThemeVersion = 0)
    LaunchedEffect(media, context, environment) {
        drawablePreloader(context, configuration, media)
    }
    LaunchedEffect(media, applicationContext) {
        videoPosterPreloader(applicationContext, media)
    }
}

private suspend fun preloadDrawables(
    context: Context,
    configuration: Configuration,
    media: List<OnboardingPageMedia>,
) {
    media.asSequence()
        .filterIsInstance<OnboardingPageMedia.Drawable>()
        .distinctBy { it.resourceId to it.resourceThemeVersion }
        .forEach { drawable ->
            val request = createDrawableLoadRequest(
                context = context,
                configuration = configuration,
                resourceThemeVersion = drawable.resourceThemeVersion,
            )
            preloadDrawable(request, drawable.resourceId)
        }
}

private suspend fun preloadDrawable(request: DrawableLoadRequest, resourceId: Int) =
    withContext(Dispatchers.IO) {
        val drawable = loadOnboardingDrawableSafely(
            loader = cachedOnboardingDrawableLoader,
            request = request,
            resourceId = resourceId,
        )
        (drawable as? BitmapDrawable)?.bitmap?.prepareToDraw()
    }

private suspend fun preloadVideoPosters(context: Context, media: List<OnboardingPageMedia>) {
    media.asSequence()
        .filterIsInstance<OnboardingPageMedia.Video>()
        .distinctBy { it.uri }
        .forEach { VideoPosterRepository.load(context, it.uri) }
}
