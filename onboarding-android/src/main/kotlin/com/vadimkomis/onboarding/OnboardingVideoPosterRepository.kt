package com.vadimkomis.onboarding

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.LruCache
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val mediaLogTag = "OnboardingMedia"
private const val posterCacheSizeKiB = 16 * 1024
private const val posterMaxWidth = 640
private const val posterMaxHeight = 1_024

internal fun interface VideoPosterFrameRetriever {
    fun getScaledFrame(maxWidth: Int, maxHeight: Int): Bitmap?
}

internal fun extractPosterFrame(
    sdkInt: Int,
    retriever: VideoPosterFrameRetriever,
): Bitmap? = if (sdkInt >= Build.VERSION_CODES.O_MR1) {
    retriever.getScaledFrame(posterMaxWidth, posterMaxHeight)
} else {
    null
}

@Composable
internal fun rememberVideoPoster(uri: Uri): ImageBitmap? {
    val context = LocalContext.current.applicationContext
    val cachedPoster = remember(uri) { VideoPosterRepository.cached(uri)?.asImageBitmap() }
    val poster by produceState(cachedPoster, context, uri) {
        if (value == null) {
            value = VideoPosterRepository.load(context, uri)?.asImageBitmap()
        }
    }
    return poster
}

internal object VideoPosterRepository {
    private val cache = object : LruCache<String, Bitmap>(posterCacheSizeKiB) {
        override fun sizeOf(key: String, value: Bitmap): Int = (value.byteCount / 1024).coerceAtLeast(1)
    }
    private val inFlightLoads = ConcurrentHashMap<String, CompletableDeferred<Bitmap?>>()

    fun cached(uri: Uri): Bitmap? = cache.get(uri.toString())

    suspend fun load(context: Context, uri: Uri): Bitmap? {
        cached(uri)?.let { return it }
        val key = uri.toString()
        val pendingLoad = CompletableDeferred<Bitmap?>()
        inFlightLoads.putIfAbsent(key, pendingLoad)?.let { return it.await() }
        return try {
            val poster = cached(uri) ?: withContext(Dispatchers.IO) {
                extractVideoPoster(context, uri)
            }
            poster?.let { cache.put(key, it) }
            pendingLoad.complete(poster)
            poster
        } catch (error: Throwable) {
            pendingLoad.completeExceptionally(error)
            throw error
        } finally {
            inFlightLoads.remove(key, pendingLoad)
        }
    }
}

private fun extractVideoPoster(context: Context, uri: Uri): Bitmap? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return null
    val platformRetriever = MediaMetadataRetriever()
    return try {
        setRetrieverDataSource(platformRetriever, context, uri)
        val retriever = MediaMetadataVideoPosterFrameRetriever(platformRetriever)
        extractPosterFrame(Build.VERSION.SDK_INT, retriever)
    } catch (error: Exception) {
        Log.w(mediaLogTag, "Unable to extract ${uri.scheme ?: "unknown"} video poster", error)
        null
    } finally {
        releaseRetriever(platformRetriever)
    }
}

@RequiresApi(Build.VERSION_CODES.O_MR1)
private class MediaMetadataVideoPosterFrameRetriever(
    private val retriever: MediaMetadataRetriever,
) : VideoPosterFrameRetriever {
    override fun getScaledFrame(maxWidth: Int, maxHeight: Int): Bitmap? =
        retriever.getScaledFrameAtTime(
            0,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
            maxWidth,
            maxHeight,
        )
}

private fun setRetrieverDataSource(
    retriever: MediaMetadataRetriever,
    context: Context,
    uri: Uri,
) {
    if (uri.scheme == "http" || uri.scheme == "https") {
        retriever.setDataSource(uri.toString(), emptyMap())
    } else {
        retriever.setDataSource(context, uri)
    }
}

private fun releaseRetriever(retriever: MediaMetadataRetriever) {
    try {
        retriever.release()
    } catch (error: RuntimeException) {
        Log.w(mediaLogTag, "Unable to release video poster retriever", error)
    }
}
