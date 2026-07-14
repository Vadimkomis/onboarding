import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    `maven-publish`
}

group = "com.vadimkomis"
version = providers.gradleProperty("VERSION_NAME").getOrElse("1.1.0-SNAPSHOT")

android {
    namespace = "com.vadimkomis.onboarding"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        getByName("androidTest").assets.directories.add(rootProject.file("Docs").absolutePath)
    }

    lint {
        abortOnError = true
        warningsAsErrors = false
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.annotation)
    api(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.group.toString()
            artifactId = "onboarding"
            version = project.version.toString()

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name = "Onboarding"
                description = "Reusable native onboarding flows for Android apps."
                url = "https://github.com/Vadimkomis/onboarding"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/mit"
                    }
                }

                scm {
                    connection = "scm:git:https://github.com/Vadimkomis/onboarding.git"
                    developerConnection = "scm:git:ssh://git@github.com/Vadimkomis/onboarding.git"
                    url = "https://github.com/Vadimkomis/onboarding"
                }
            }
        }
    }
}
