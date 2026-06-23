
plugins {
    id("delta.android.application")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
}

fun getGitCommitCount(): Int {
    return try {
        providers.exec {
            commandLine("git", "rev-list", "--count", "HEAD")
        }.standardOutput.asText.get().trim().toInt()
    } catch (_: Exception) {
        1
    }
}

android {
    namespace = "com.delta.group"

    defaultConfig {
        applicationId = "com.delta.group"
        versionCode = getGitCommitCount()
        versionName = "1.0.${getGitCommitCount()}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        release {
            optimization {
                enable = false
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("daily") {
            dimension = "environment"
            applicationIdSuffix = ".daily"
            versionNameSuffix = "-daily"
            buildConfigField("String", "API_BASE_URL", "\"https://daily-api.example.com/\"")
            buildConfigField("String", "API_HOST_HEADER", "\"daily-api.example.com\"")
        }
        create("online") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"https://api.example.com/\"")
            buildConfigField("String", "API_HOST_HEADER", "\"\"")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":features:feature-home"))
    implementation(project(":core:core-common"))
    implementation(project(":core:core-network"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.bundles.test.unit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.bundles.test.android)

    debugImplementation(libs.bundles.compose.debug)
}
