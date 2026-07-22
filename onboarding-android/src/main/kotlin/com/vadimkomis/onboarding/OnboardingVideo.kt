package com.vadimkomis.onboarding

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

private const val mediaLogTag = "OnboardingMedia"

internal interface OnboardingVideoPlayer {
    val player: Player?

    fun play()
    fun pause()
    fun release()
}

internal fun interface OnboardingVideoPlayerFactory {
    fun create(
        context: Context,
        uri: Uri,
        onFirstFrame: () -> Unit,
    ): OnboardingVideoPlayer
}

internal val media3VideoPlayerFactory = OnboardingVideoPlayerFactory { context, uri, callback ->
    createMedia3VideoPlayer(context, uri, callback)
}

@Composable
internal fun OnboardingVideo(
    uri: Uri,
    isActive: Boolean,
    playerFactory: OnboardingVideoPlayerFactory,
    modifier: Modifier,
) {
    var renderedFirstFrame by remember(uri) { mutableStateOf(false) }
    val poster = rememberVideoPoster(uri)
    val lifecyclePlayer = rememberLifecycleVideoPlayer(
        uri = uri,
        playerFactory = playerFactory,
        onFirstFrame = { renderedFirstFrame = true },
        onPlayerReleased = { renderedFirstFrame = false },
    )

    LaunchedEffect(lifecyclePlayer.videoPlayer, lifecyclePlayer.canPlay, isActive) {
        if (isActive && lifecyclePlayer.canPlay) lifecyclePlayer.videoPlayer?.play()
        else lifecyclePlayer.videoPlayer?.pause()
    }
    Box(modifier = modifier) {
        lifecyclePlayer.videoPlayer?.player?.let { VideoPlayerView(it, Modifier.fillMaxSize()) }
        if (!renderedFirstFrame && poster != null) {
            Image(
                bitmap = poster,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(OnboardingTestTags.videoPoster),
            )
        }
    }
}

private data class LifecycleVideoPlayer(
    val videoPlayer: OnboardingVideoPlayer?,
    val canPlay: Boolean,
)

@Composable
private fun rememberLifecycleVideoPlayer(
    uri: Uri,
    playerFactory: OnboardingVideoPlayerFactory,
    onFirstFrame: () -> Unit,
    onPlayerReleased: () -> Unit,
): LifecycleVideoPlayer {
    val context = LocalContext.current.applicationContext
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var videoPlayer by remember(uri, playerFactory) { mutableStateOf<OnboardingVideoPlayer?>(null) }
    var canPlay by remember(uri, playerFactory) {
        mutableStateOf(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }

    DisposableEffect(lifecycle, uri, playerFactory) {
        fun startPlayer() {
            if (videoPlayer == null) {
                videoPlayer = createVideoPlayerSafely(context, uri, playerFactory, onFirstFrame)
            }
        }
        fun releasePlayer() {
            videoPlayer?.release()
            videoPlayer = null
            canPlay = false
            onPlayerReleased()
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> startPlayer()
                Lifecycle.Event.ON_RESUME -> canPlay = true
                Lifecycle.Event.ON_PAUSE -> {
                    canPlay = false
                    videoPlayer?.pause()
                }
                Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> releasePlayer()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) startPlayer()
        onDispose {
            lifecycle.removeObserver(observer)
            releasePlayer()
        }
    }
    return LifecycleVideoPlayer(videoPlayer, canPlay)
}

private fun createVideoPlayerSafely(
    context: Context,
    uri: Uri,
    factory: OnboardingVideoPlayerFactory,
    onFirstFrame: () -> Unit,
): OnboardingVideoPlayer? = try {
    factory.create(context, uri, onFirstFrame)
} catch (error: RuntimeException) {
    Log.w(mediaLogTag, "Unable to create ${uri.scheme ?: "unknown"} video player", error)
    null
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerView(player: Player, modifier: Modifier) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                this.player = player
            }
        },
        update = { it.player = player },
        modifier = modifier,
    )
}

@OptIn(UnstableApi::class)
internal fun createMedia3VideoPlayer(
    context: Context,
    uri: Uri,
    onFirstFrame: () -> Unit,
): OnboardingVideoPlayer {
    val player = ExoPlayer.Builder(context).build()
    try {
        player.volume = 0f
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.playWhenReady = false
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
    } catch (error: RuntimeException) {
        player.release()
        throw error
    }
    return Media3OnboardingVideoPlayer(player, onFirstFrame)
}

private class Media3OnboardingVideoPlayer(
    private val exoPlayer: ExoPlayer,
    onFirstFrame: () -> Unit,
) : OnboardingVideoPlayer {
    private val listener = object : Player.Listener {
        override fun onRenderedFirstFrame() = onFirstFrame()
    }

    init {
        exoPlayer.addListener(listener)
    }

    override val player: Player = exoPlayer

    override fun play() {
        exoPlayer.playWhenReady = true
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
        exoPlayer.pause()
    }

    override fun release() {
        exoPlayer.removeListener(listener)
        exoPlayer.release()
    }
}
