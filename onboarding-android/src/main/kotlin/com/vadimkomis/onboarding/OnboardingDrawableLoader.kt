package com.vadimkomis.onboarding

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.LruCache

private const val mediaLogTag = "OnboardingMedia"
private const val drawableCacheSizeBytes = 16 * 1_024 * 1_024

internal fun interface OnboardingDrawableLoader {
    fun load(request: DrawableLoadRequest, resourceId: Int): Drawable
}

internal data class DrawableEnvironmentKey(
    val configuration: Configuration,
    val contextIdentity: Int,
    val resourcesIdentity: Int,
    val themeIdentity: Int,
    val resourceThemeVersion: Int,
)

internal data class DrawableLoadRequest(
    val sourceContext: Context,
    val resources: Resources,
    val theme: Resources.Theme,
    val environment: DrawableEnvironmentKey,
)

internal val cachedOnboardingDrawableLoader = OnboardingDrawableLoader { request, resourceId ->
    OnboardingDrawableRepository.load(request, resourceId)
}

internal fun drawableEnvironmentKey(
    context: Context,
    configuration: Configuration,
    resourceThemeVersion: Int,
): DrawableEnvironmentKey = DrawableEnvironmentKey(
    configuration = Configuration(configuration),
    contextIdentity = System.identityHashCode(context),
    resourcesIdentity = System.identityHashCode(context.resources),
    themeIdentity = System.identityHashCode(context.theme),
    resourceThemeVersion = resourceThemeVersion,
)

internal fun createDrawableLoadRequest(
    context: Context,
    configuration: Configuration,
    resourceThemeVersion: Int,
): DrawableLoadRequest {
    val environment = drawableEnvironmentKey(context, configuration, resourceThemeVersion)
    val configuredResources = context
        .createConfigurationContext(environment.configuration)
        .resources
    val themeSnapshot = configuredResources.newTheme().apply { setTo(context.theme) }
    return DrawableLoadRequest(
        sourceContext = context,
        resources = configuredResources,
        theme = themeSnapshot,
        environment = environment,
    )
}

internal fun loadOnboardingDrawableSafely(
    loader: OnboardingDrawableLoader,
    request: DrawableLoadRequest,
    resourceId: Int,
): Drawable? = try {
    loader.load(request, resourceId)
} catch (error: Resources.NotFoundException) {
    Log.w(mediaLogTag, "Unable to display drawable resource", error)
    null
}

internal fun Drawable.bitmapCacheSizeBytesOrNull(): Int? =
    (this as? BitmapDrawable)?.bitmap?.allocationByteCount

private object OnboardingDrawableRepository {
    private val cacheLock = Any()
    private val cache = object : LruCache<DrawableCacheKey, CachedDrawableState>(
        drawableCacheSizeBytes,
    ) {
        override fun sizeOf(key: DrawableCacheKey, value: CachedDrawableState): Int =
            value.sizeBytes
    }

    fun load(request: DrawableLoadRequest, resourceId: Int): Drawable = synchronized(cacheLock) {
        val key = DrawableCacheKey(resourceId, request.environment)
        cache.get(key)?.newDrawable(request.resources, request.theme)
            ?: loadAndCache(request, resourceId, key)
    }

    private fun loadAndCache(
        request: DrawableLoadRequest,
        resourceId: Int,
        key: DrawableCacheKey,
    ): Drawable {
        val drawable = request.resources.getDrawable(resourceId, request.theme)
        val sizeBytes = drawable.bitmapCacheSizeBytesOrNull()
        if (sizeBytes != null && sizeBytes <= drawableCacheSizeBytes) {
            drawable.constantState?.let { state ->
                cache.put(key, CachedDrawableState(state, sizeBytes.coerceAtLeast(1)))
            }
        }
        return drawable.mutate()
    }
}

private data class DrawableCacheKey(
    val resourceId: Int,
    val environment: DrawableEnvironmentKey,
)

private data class CachedDrawableState(
    val state: Drawable.ConstantState,
    val sizeBytes: Int,
) {
    fun newDrawable(resources: Resources, theme: Resources.Theme): Drawable =
        state.newDrawable(resources, theme).mutate()
}
