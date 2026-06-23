import com.android.build.api.variant.BuildConfigField

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

fun org.gradle.api.Project.gradleString(name: String, default: String): String =
    providers.gradleProperty(name).orNull?.trim()?.takeIf { it.isNotEmpty() } ?: default

fun org.gradle.api.Project.gradleBoolean(name: String, default: Boolean): Boolean =
    providers.gradleProperty(name).orNull?.trim()?.toBooleanStrictOrNull() ?: default

fun String.asBuildConfigString(): String =
    "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""

android {
    namespace = "com.delta.helper"

    defaultConfig {
        applicationId = "com.delta.helper"
        versionCode = getGitCommitCount()
        versionName = "1.0.${getGitCommitCount()}"

        buildConfigField("String", "CLIENT_CHANNEL", "\"official\"")
        buildConfigField(
            "String",
            "PUBLISHER_KEY",
            gradleString("publisherKey", "official").asBuildConfigString(),
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val storeFilePath = providers.gradleProperty("RELEASE_STORE_FILE").orNull?.trim()
            if (!storeFilePath.isNullOrEmpty()) {
                storeFile = rootProject.file(storeFilePath)
                storePassword = providers.gradleProperty("RELEASE_STORE_PASSWORD").orNull
                keyAlias = providers.gradleProperty("RELEASE_KEY_ALIAS").orNull
                keyPassword = providers.gradleProperty("RELEASE_KEY_PASSWORD").orNull
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile?.exists() == true) {
                signingConfig = releaseSigning
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("daily") {
            dimension = "environment"
            applicationIdSuffix = ".daily"
            versionNameSuffix = "-daily"
            buildConfigField("String", "BUILD_ENV", "\"daily\"")
            buildConfigField(
                "String",
                "API_BASE_URL",
                gradleString("dailyApiBaseUrl", "http://10.0.2.2:8080/").asBuildConfigString(),
            )
            buildConfigField(
                "String",
                "API_HOST_HEADER",
                gradleString("dailyApiHostHeader", "").asBuildConfigString(),
            )
            buildConfigField("String", "CLIENT_CHANNEL", "\"daily\"")
        }
        create("online") {
            dimension = "environment"
            buildConfigField("String", "BUILD_ENV", "\"online\"")
            buildConfigField(
                "String",
                "API_BASE_URL",
                gradleString("onlineApiBaseUrl", "https://monster.hk.cn/").asBuildConfigString(),
            )
            buildConfigField(
                "String",
                "API_HOST_HEADER",
                gradleString("onlineApiHostHeader", "").asBuildConfigString(),
            )
            buildConfigField("String", "CLIENT_CHANNEL", "\"official\"")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

androidComponents {
    onVariants(androidComponents.selector().all()) { variant ->
        val environment = variant.productFlavors
            .firstOrNull { it.first == "environment" }
            ?.second
        val isDaily = environment == "daily"
        val isDebug = variant.buildType == "debug"

        val mockAlreadyActivated = when {
            !isDaily -> false
            isDebug -> gradleBoolean("dailyDebugMockAlreadyActivated", true)
            else -> gradleBoolean("dailyReleaseMockAlreadyActivated", false)
        }
        val simulateNotActivated = when {
            !isDaily -> false
            isDebug -> gradleBoolean("dailyDebugSimulateNotActivated", true)
            else -> gradleBoolean("dailyReleaseSimulateNotActivated", false)
        }
        val httpLoggingEnabled = if (isDebug) {
            gradleBoolean("debugHttpLogging", true)
        } else {
            gradleBoolean("releaseHttpLogging", false)
        }

        variant.buildConfigFields?.apply {
            put(
                "MOCK_ALREADY_ACTIVATED",
                BuildConfigField("boolean", mockAlreadyActivated.toString(), null),
            )
            put(
                "SIMULATE_NOT_ACTIVATED",
                BuildConfigField("boolean", simulateNotActivated.toString(), null),
            )
            put(
                "HTTP_LOGGING_ENABLED",
                BuildConfigField("boolean", httpLoggingEnabled.toString(), null),
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-network"))
    implementation(project(":core:core-activation"))

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.savedstate)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.bundles.test.unit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.bundles.test.android)

    debugImplementation(libs.bundles.compose.debug)
}

val adbReverseDevApi = tasks.register("adbReverseDevApi") {
    group = "android"
    description = "Forward emulator localhost:8080 to host :8080 for local delta-api"
    doLast {
        providers.exec {
            commandLine("adb", "reverse", "tcp:8080", "tcp:8080")
            isIgnoreExitValue = true
        }.result.get()
    }
}

tasks.matching { it.name == "installDailyDebug" }.configureEach {
    dependsOn(adbReverseDevApi)
}
