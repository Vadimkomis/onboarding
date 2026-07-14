package com.vadimkomis.onboarding

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OnboardingMediaTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun drawableExposesItsContentDescription() {
        composeRule.setContent {
            OnboardingMedia(
                media = OnboardingPageMedia.Drawable(
                    resourceId = android.R.drawable.ic_dialog_info,
                    contentDescription = "Preview image",
                ),
                theme = OnboardingTheme.standard,
                isActive = true,
                modifier = Modifier.size(200.dp, 320.dp),
            )
        }

        composeRule.onNodeWithContentDescription("Preview image").assertIsDisplayed()
    }

    @Test
    fun videoPlaysOnlyWhileActiveAndReleasesWhenDisposed() {
        val factory = FakeVideoPlayerFactory()
        val lifecycleOwner = TestLifecycleOwner()
        var isActive by mutableStateOf(true)
        var isVisible by mutableStateOf(true)
        composeRule.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                if (isVisible) {
                    OnboardingMedia(
                        media = OnboardingPageMedia.Video(Uri.EMPTY, "Demo video"),
                        theme = OnboardingTheme.standard,
                        isActive = isActive,
                        modifier = Modifier.size(200.dp, 320.dp),
                        playerFactory = factory,
                    )
                }
            }
        }

        composeRule.runOnIdle { lifecycleOwner.resume() }
        composeRule.waitUntil(timeoutMillis = 5_000) { factory.player.playCount.get() > 0 }
        composeRule.runOnIdle { isActive = false }
        composeRule.waitUntil(timeoutMillis = 5_000) { factory.player.pauseCount.get() > 0 }
        composeRule.runOnIdle { isActive = true }
        composeRule.waitUntil(timeoutMillis = 5_000) { factory.player.playCount.get() > 1 }
        composeRule.runOnIdle { lifecycleOwner.pause() }
        composeRule.waitUntil(timeoutMillis = 5_000) { factory.player.pauseCount.get() > 1 }
        composeRule.runOnIdle { lifecycleOwner.resumeFromPause() }
        composeRule.waitUntil(timeoutMillis = 5_000) { factory.player.playCount.get() > 2 }
        composeRule.runOnIdle { isVisible = false }
        composeRule.waitUntil(timeoutMillis = 5_000) { factory.player.releaseCount.get() > 0 }

        assertEquals(1, factory.createCount.get())
        assertEquals(1, factory.player.releaseCount.get())
    }

    @Test
    fun media3VideoStartsMutedLoopingAndPaused() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fixture = copyVideoFixture("onboarding-player-config")

        try {
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val videoPlayer = createMedia3VideoPlayer(
                    context,
                    Uri.fromFile(fixture),
                    onFirstFrame = {},
                )
                val player = requireNotNull(videoPlayer.player)

                assertEquals(0f, player.volume)
                assertEquals(Player.REPEAT_MODE_ONE, player.repeatMode)
                assertFalse(player.playWhenReady)

                videoPlayer.release()
            }
        } finally {
            fixture.delete()
        }
    }

    @Test
    fun preloaderExtractsAndCachesRealVideoPoster() {
        val fixture = copyVideoFixture("onboarding-preload")
        val uri = Uri.fromFile(fixture)

        try {
            composeRule.setContent {
                PreloadOnboardingMedia(listOf(OnboardingPageMedia.Video(uri)))
            }
            composeRule.waitUntil(timeoutMillis = 10_000) {
                VideoPosterRepository.cached(uri) != null
            }

            val poster = requireNotNull(VideoPosterRepository.cached(uri))
            assertTrue(poster.width > 0)
            assertTrue(poster.height > 0)
        } finally {
            fixture.delete()
        }
    }

    @Test
    fun posterRemainsUntilFirstFrameThenDisappears() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fixture = copyVideoFixture("onboarding-fake-first-frame")
        val uri = Uri.fromFile(fixture)
        val factory = FakeVideoPlayerFactory()
        val lifecycleOwner = TestLifecycleOwner()

        try {
            assertNotNull(runBlocking {
                withTimeout(10_000) { VideoPosterRepository.load(context, uri) }
            })
            composeRule.setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                    OnboardingMedia(
                        media = OnboardingPageMedia.Video(uri, "Demo video"),
                        theme = OnboardingTheme.standard,
                        isActive = true,
                        modifier = Modifier.size(200.dp, 320.dp),
                        playerFactory = factory,
                    )
                }
            }

            composeRule.runOnIdle { lifecycleOwner.resume() }
            composeRule.onNodeWithTag(OnboardingTestTags.videoPoster).assertIsDisplayed()
            composeRule.runOnIdle { factory.renderFirstFrame() }
            composeRule.onNodeWithTag(OnboardingTestTags.videoPoster).assertDoesNotExist()
        } finally {
            fixture.delete()
        }
    }

    @Test
    fun realVideoPlaybackRemovesPosterAfterFirstFrame() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fixture = copyVideoFixture("onboarding-real-first-frame")
        val uri = Uri.fromFile(fixture)
        val lifecycleOwner = TestLifecycleOwner()
        var isVisible by mutableStateOf(true)

        try {
            assertNotNull(runBlocking {
                withTimeout(10_000) { VideoPosterRepository.load(context, uri) }
            })
            composeRule.setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                    if (isVisible) {
                        OnboardingMedia(
                            media = OnboardingPageMedia.Video(uri, "Real demo video"),
                            theme = OnboardingTheme.standard,
                            isActive = true,
                            modifier = Modifier.size(200.dp, 320.dp),
                        )
                    }
                }
            }

            composeRule.onNodeWithTag(OnboardingTestTags.videoPoster).assertIsDisplayed()
            composeRule.runOnIdle { lifecycleOwner.resume() }
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodesWithTag(OnboardingTestTags.videoPoster)
                    .fetchSemanticsNodes()
                    .isEmpty()
            }
        } finally {
            composeRule.runOnIdle { isVisible = false }
            fixture.delete()
        }
    }

    private fun copyVideoFixture(fileNamePrefix: String): File {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val destination = File.createTempFile(
            fileNamePrefix,
            ".mp4",
            instrumentation.targetContext.cacheDir,
        )
        instrumentation.context.assets.open("onboarding-demo.mp4").use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        }
        return destination
    }
}

private class TestLifecycleOwner : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = registry

    fun resume() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun pause() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    fun resumeFromPause() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
}

private class FakeVideoPlayerFactory : OnboardingVideoPlayerFactory {
    val createCount = AtomicInteger()
    val player = FakeVideoPlayer()
    private var onFirstFrame: () -> Unit = {}

    override fun create(
        context: Context,
        uri: Uri,
        onFirstFrame: () -> Unit,
    ): OnboardingVideoPlayer {
        createCount.incrementAndGet()
        this.onFirstFrame = onFirstFrame
        return player
    }

    fun renderFirstFrame() {
        onFirstFrame()
    }
}

private class FakeVideoPlayer : OnboardingVideoPlayer {
    override val player: Player? = null
    val playCount = AtomicInteger()
    val pauseCount = AtomicInteger()
    val releaseCount = AtomicInteger()

    override fun play() {
        playCount.incrementAndGet()
    }

    override fun pause() {
        pauseCount.incrementAndGet()
    }

    override fun release() {
        releaseCount.incrementAndGet()
    }
}
