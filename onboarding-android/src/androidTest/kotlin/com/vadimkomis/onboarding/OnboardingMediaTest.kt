package com.vadimkomis.onboarding

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Looper
import android.view.ContextThemeWrapper
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
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
    fun drawableLoaderRunsOffMainThread() {
        val loader = RecordingDrawableLoader()

        composeRule.setContent {
            OnboardingMedia(
                media = OnboardingPageMedia.Drawable(android.R.drawable.ic_dialog_info),
                theme = OnboardingTheme.standard,
                isActive = true,
                modifier = Modifier.size(200.dp, 320.dp),
                drawableLoader = loader,
            )
        }
        waitForDrawableLoads(loader, expectedCount = 1)

        assertFalse(loader.records.single().thread === Looper.getMainLooper().thread)
    }

    @Test
    fun drawableReloadsAfterConfigurationChangesWithoutRecreatingComposition() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val loader = RecordingDrawableLoader()
        var configuration by mutableStateOf(Configuration(context.resources.configuration))

        composeRule.setContent {
            CompositionLocalProvider(
                LocalContext provides context,
                LocalConfiguration provides configuration,
            ) {
                OnboardingMedia(
                    media = OnboardingPageMedia.Drawable(android.R.drawable.ic_dialog_info),
                    theme = OnboardingTheme.standard,
                    isActive = true,
                    modifier = Modifier.size(200.dp, 320.dp),
                    drawableLoader = loader,
                )
            }
        }
        waitForDrawableLoads(loader, expectedCount = 1)

        composeRule.runOnIdle { configuration = configuration.withToggledNightMode() }
        waitForDrawableLoads(loader, expectedCount = 2)
        composeRule.runOnIdle { configuration = configuration.withDifferentLocale() }
        waitForDrawableLoads(loader, expectedCount = 3)
        composeRule.runOnIdle { configuration = configuration.withDifferentDensity() }
        waitForDrawableLoads(loader, expectedCount = 4)

        assertEquals(4, loader.records.size)
    }

    @Test
    fun drawableReloadsWhenThemedContextChangesWithoutRecreatingComposition() {
        val baseContext = InstrumentationRegistry.getInstrumentation().targetContext
        val initialContext = ContextThemeWrapper(baseContext, android.R.style.Theme_Material_Light)
        val replacementContext = ContextThemeWrapper(baseContext, android.R.style.Theme_Material)
        val loader = RecordingDrawableLoader()
        var themedContext by mutableStateOf<Context>(initialContext)

        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides themedContext) {
                OnboardingMedia(
                    media = OnboardingPageMedia.Drawable(android.R.drawable.ic_dialog_info),
                    theme = OnboardingTheme.standard,
                    isActive = true,
                    modifier = Modifier.size(200.dp, 320.dp),
                    drawableLoader = loader,
                )
            }
        }
        waitForDrawableLoads(loader, expectedCount = 1)

        composeRule.runOnIdle { themedContext = replacementContext }
        waitForDrawableLoads(loader, expectedCount = 2)

        assertEquals(listOf(initialContext, replacementContext), loader.records.map { it.context })
    }

    @Test
    fun drawableReloadsWhenResourceThemeVersionChangesOnSameContext() {
        val baseContext = InstrumentationRegistry.getInstrumentation().targetContext
        val themedContext = ContextThemeWrapper(baseContext, android.R.style.Theme_Material_Light)
        val loader = RecordingDrawableLoader()
        var resourceThemeVersion by mutableStateOf(0)

        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides themedContext) {
                OnboardingMedia(
                    media = OnboardingPageMedia.Drawable(
                        resourceId = android.R.drawable.ic_dialog_info,
                        resourceThemeVersion = resourceThemeVersion,
                    ),
                    theme = OnboardingTheme.standard,
                    isActive = true,
                    modifier = Modifier.size(200.dp, 320.dp),
                    drawableLoader = loader,
                )
            }
        }
        waitForDrawableLoads(loader, expectedCount = 1)

        composeRule.runOnIdle {
            themedContext.setTheme(android.R.style.Theme_Material)
            resourceThemeVersion += 1
        }
        waitForDrawableLoads(loader, expectedCount = 2)

        assertEquals(listOf(themedContext, themedContext), loader.records.map { it.context })
    }

    @Test
    fun drawableLoadRequestCopiesTheMutableContextTheme() {
        val baseContext = InstrumentationRegistry.getInstrumentation().targetContext
        val context = ContextThemeWrapper(baseContext, android.R.style.Theme_Material_Light)
        lateinit var request: DrawableLoadRequest

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            request = createDrawableLoadRequest(
                context = context,
                configuration = Configuration(context.resources.configuration),
                resourceThemeVersion = 7,
            )
        }

        assertNotSame(context.theme, request.theme)
        assertEquals(7, request.environment.resourceThemeVersion)
    }

    @Test
    fun compositeDrawableIsNotEligibleForTheBitmapCache() {
        val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources
        val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
        val bitmapDrawable = BitmapDrawable(resources, bitmap)
        val composite = LayerDrawable(arrayOf(bitmapDrawable))

        try {
            assertNull(composite.bitmapCacheSizeBytesOrNull())
            assertEquals(bitmap.allocationByteCount, bitmapDrawable.bitmapCacheSizeBytesOrNull())
        } finally {
            bitmap.recycle()
        }
    }

    @Test
    fun cachedDrawableLoaderReturnsDistinctInstances() {
        val baseContext = InstrumentationRegistry.getInstrumentation().targetContext
        val context = ContextThemeWrapper(baseContext, android.R.style.Theme_Material_Light)

        val request = createDrawableLoadRequest(
            context = context,
            configuration = Configuration(context.resources.configuration),
            resourceThemeVersion = 0,
        )
        val first = cachedOnboardingDrawableLoader.load(
            request,
            android.R.drawable.ic_dialog_info,
        )
        val second = cachedOnboardingDrawableLoader.load(
            request,
            android.R.drawable.ic_dialog_info,
        )

        assertFalse(first === second)
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
    fun configurationChangeDoesNotRestartVideoPosterPreloading() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val media = listOf(OnboardingPageMedia.Video(Uri.EMPTY))
        val posterPreloadStarted = CompletableDeferred<Unit>()
        val allowPosterPreloadToFinish = CompletableDeferred<Unit>()
        val posterPreloadFinished = CompletableDeferred<Unit>()
        val drawablePreloadStarted = CompletableDeferred<Unit>()
        val drawableReloaded = CompletableDeferred<Unit>()
        val drawablePreloadCount = AtomicInteger()
        val posterPreloadCount = AtomicInteger()
        var configuration by mutableStateOf(Configuration(context.resources.configuration))

        composeRule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                PreloadOnboardingMedia(
                    media = media,
                    drawablePreloader = { _, _, _ ->
                        when (drawablePreloadCount.incrementAndGet()) {
                            1 -> drawablePreloadStarted.complete(Unit)
                            2 -> drawableReloaded.complete(Unit)
                        }
                    },
                    videoPosterPreloader = { _, _ ->
                        posterPreloadCount.incrementAndGet()
                        posterPreloadStarted.complete(Unit)
                        allowPosterPreloadToFinish.await()
                        posterPreloadFinished.complete(Unit)
                    },
                )
            }
        }
        awaitSignal("drawable preload to start", drawablePreloadStarted)
        awaitSignal("poster preload to start", posterPreloadStarted)

        composeRule.runOnIdle { configuration = configuration.withToggledNightMode() }
        awaitSignal("drawable preload to restart", drawableReloaded)
        allowPosterPreloadToFinish.complete(Unit)
        awaitSignal("poster preload to finish", posterPreloadFinished)

        assertEquals(1, posterPreloadCount.get())
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

    private fun waitForDrawableLoads(loader: RecordingDrawableLoader, expectedCount: Int) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            loader.records.size >= expectedCount
        }
    }

    private fun awaitSignal(description: String, signal: CompletableDeferred<Unit>) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            signal.isCompleted
        }
        assertTrue("$description was cancelled", !signal.isCancelled)
        runBlocking { signal.await() }
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

private data class DrawableLoadRecord(
    val context: Context,
    val thread: Thread,
)

private class RecordingDrawableLoader : OnboardingDrawableLoader {
    val records = CopyOnWriteArrayList<DrawableLoadRecord>()

    override fun load(request: DrawableLoadRequest, resourceId: Int) =
        ColorDrawable(android.graphics.Color.MAGENTA).also {
            records += DrawableLoadRecord(request.sourceContext, Thread.currentThread())
        }
}

private fun Configuration.withToggledNightMode(): Configuration = Configuration(this).apply {
    val nightMode = if (uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
        Configuration.UI_MODE_NIGHT_NO
    } else {
        Configuration.UI_MODE_NIGHT_YES
    }
    uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or nightMode
}

@Suppress("DEPRECATION")
private fun Configuration.withDifferentLocale(): Configuration = Configuration(this).apply {
    setLocale(if (locale == Locale.JAPAN) Locale.FRANCE else Locale.JAPAN)
}

private fun Configuration.withDifferentDensity(): Configuration = Configuration(this).apply {
    densityDpi = if (densityDpi == 320) 480 else 320
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
