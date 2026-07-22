package com.vadimkomis.onboarding

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadmeQuickStartTest {
    @Test
    fun androidQuickStartUsesTheRuntimePackageNameWithoutBuildConfig() {
        val quickStart = androidQuickStart()

        assertTrue(
            "Android quick start should build resource URIs from context.packageName",
            quickStart.contains("context.packageName"),
        )
        val expectedResourceUri =
            """Uri.parse("android.resource://${'$'}packageName/${'$'}{R.raw.onboarding_demo}")"""
        assertTrue(
            "Android quick start should interpolate the runtime package into the resource URI",
            quickStart.contains(expectedResourceUri),
        )
        assertFalse(
            "Android quick start should not require generated BuildConfig fields",
            quickStart.contains("BuildConfig"),
        )
    }

    private fun androidQuickStart(): String {
        val readme = findReadme()
        val contents = readme.readText()
        val heading = "### Android quick start"
        val headingIndex = contents.indexOf(heading)

        check(headingIndex >= 0) { "README is missing the Android quick-start section" }
        return contents
            .substring(headingIndex + heading.length)
            .substringBefore("\n## ")
    }

    private fun findReadme(): File {
        val workingDirectory = requireNotNull(System.getProperty("user.dir")) {
            "user.dir must be available while running tests"
        }
        return generateSequence(File(workingDirectory).absoluteFile) { current ->
            current.parentFile
        }.map { directory ->
            directory.resolve("README.md")
        }.firstOrNull(File::isFile)
            ?: error("Unable to locate the repository README")
    }
}
