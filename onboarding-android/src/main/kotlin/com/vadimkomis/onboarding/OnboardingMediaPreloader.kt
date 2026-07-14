package com.vadimkomis.onboarding

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val mediaLogTag = "OnboardingMedia"

@Composable
internal fun PreloadOnboardingMedia(media: List<OnboardingPageMedia>) {
    val context = LocalContext.current.applicationContext
    LaunchedEffect(media, context) {
        coroutineScope {
            launch { preloadDrawables(context, media) }
            launch { preloadVideoPosters(context, media) }
        }
    }
}

private suspend fun preloadDrawables(context: Context, media: List<OnboardingPageMedia>) {
    media.asSequence()
        .filterIsInstance<OnboardingPageMedia.Drawable>()
        .distinctBy { it.resourceId }
        .forEach { preloadDrawable(context, it.resourceId) }
}

private suspend fun preloadDrawable(context: Context, resourceId: Int) = withContext(Dispatchers.IO) {
    try {
        val drawable = context.resources.getDrawable(resourceId, context.theme)
        (drawable as? BitmapDrawable)?.bitmap?.prepareToDraw()
    } catch (error: Resources.NotFoundException) {
        Log.w(mediaLogTag, "Unable to preload drawable resource", error)
    }
}

private suspend fun preloadVideoPosters(context: Context, media: List<OnboardingPageMedia>) {
    media.asSequence()
        .filterIsInstance<OnboardingPageMedia.Video>()
        .distinctBy { it.uri }
        .forEach { VideoPosterRepository.load(context, it.uri) }
}
