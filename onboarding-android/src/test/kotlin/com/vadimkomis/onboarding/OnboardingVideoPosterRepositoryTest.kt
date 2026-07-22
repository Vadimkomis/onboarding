package com.vadimkomis.onboarding

import android.graphics.Bitmap
import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingVideoPosterRepositoryTest {
    @Test
    fun api23Through26SkipPosterFrameExtraction() {
        for (sdkInt in 23..26) {
            val retriever = RecordingVideoPosterFrameRetriever()

            val poster = extractPosterFrame(sdkInt, retriever)

            assertEquals(null, poster)
            assertEquals(emptyList<Pair<Int, Int>>(), retriever.scaledFrameRequests)
        }
    }

    @Test
    fun api27AndNewerRequestOnlyABoundedScaledFrame() {
        listOf(27, 28, 36, Int.MAX_VALUE).forEach { sdkInt ->
            val retriever = RecordingVideoPosterFrameRetriever()

            extractPosterFrame(sdkInt, retriever)

            assertEquals(listOf(640 to 1_024), retriever.scaledFrameRequests)
        }
    }
}

private class RecordingVideoPosterFrameRetriever : VideoPosterFrameRetriever {
    val scaledFrameRequests = mutableListOf<Pair<Int, Int>>()

    override fun getScaledFrame(maxWidth: Int, maxHeight: Int): Bitmap? {
        scaledFrameRequests += maxWidth to maxHeight
        return null
    }
}
