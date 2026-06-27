import com.android.build.api.variant.BuildConfigField
import java.util.Locale
import java.util.Properties

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

fun org.gradle.api.Project.loadReleaseSigningProperties(): Properties {
    val props = Properties()
    val signingFile = file("key/signing.properties")
    if (signingFile.exists()) {
        signingFile.inputStream().use { stream -> props.load(stream) }
    }
    return props
}

fun org.gradle.api.Project.releaseSigningValue(
    props: Properties,
    propertyKey: String,
    gradlePropertyKey: String,
): String? =
    props.getProperty(propertyKey)?.trim()?.takeIf { it.isNotEmpty() }
        ?: providers.gradleProperty(gradlePropertyKey).orNull?.trim()?.takeIf { it.isNotEmpty() }

fun String.capitalizeVariantName(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

fun org.gradle.api.Project.resolveSigningStoreFile(storeFilePath: String): java.io.File {
    val candidates = listOf(
        file(storeFilePath),
        file("key/$storeFilePath"),
        rootProject.file(storeFilePath),
        rootProject.file("apps/delta-helper-app/$storeFilePath"),
        rootProject.file("apps/delta-helper-app/key/$storeFilePath"),
    )
    return candidates.firstOrNull { it.exists() } ?: file(storeFilePath)
}

fun com.android.build.api.dsl.SigningConfig.isReady(): Boolean =
    storeFile?.exists() == true &&
        !storePassword.isNullOrBlank() &&
        !keyAlias.isNullOrBlank() &&
        !keyPassword.isNullOrBlank()

android {
    namespace = "com.delta.helper"

    defaultConfig {
        applicationId = "com.delta.helper"
        versionCode = 103
        versionName = "1.0.3"

        missingDimensionStrategy("environment", "online")

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
            val signingProps = loadReleaseSigningProperties()
            val storeFilePath = releaseSigningValue(signingProps, "storeFile", "RELEASE_STORE_FILE")
                ?: "key/key.jks"
            storeFile = resolveSigningStoreFile(storeFilePath)
            storePassword = releaseSigningValue(signingProps, "storePassword", "RELEASE_STORE_PASSWORD")
            keyAlias = releaseSigningValue(signingProps, "keyAlias", "RELEASE_KEY_ALIAS")
            keyPassword = releaseSigningValue(signingProps, "keyPassword", "RELEASE_KEY_PASSWORD")
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
            signingConfig = when {
                releaseSigning.isReady() -> releaseSigning
                else -> signingConfigs.getByName("debug")
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
                gradleString("dailyApiBaseUrl", "https://monster.hk.cn/").asBuildConfigString(),
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

val defaultVersionName = android.defaultConfig.versionName ?: "unknown"
val defaultVersionCode = android.defaultConfig.versionCode ?: 0

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

    onVariants(
        androidComponents.selector()
            .withFlavor("environment", "online")
            .withBuildType("release"),
    ) { variant ->
        val variantName = variant.name
        val artifactBaseName = "delta-helper-online-v$defaultVersionName-$defaultVersionCode"
        val variantCapitalized = variantName.capitalizeVariantName()
        val apkFileName = "$artifactBaseName.apk"
        val aabFileName = "$artifactBaseName.aab"

        val renameApkTask = tasks.register<RenamePackagedArtifactTask>("rename${variantCapitalized}Apk") {
            group = "build"
            description = "Rename $variantName APK to include version"
            dependsOn("package$variantCapitalized")
            sourceDirectoryPaths.set(
                layout.buildDirectory.dir("outputs/apk/online/release").map { dir ->
                    listOf(dir.asFile.absolutePath)
                },
            )
            artifactExtension.set("apk")
            outputFileName.set(apkFileName)
        }

        val renameBundleTask = tasks.register<RenamePackagedArtifactTask>("rename${variantCapitalized}Bundle") {
            group = "build"
            description = "Rename $variantName AAB to include version"
            dependsOn("bundle$variantCapitalized")
            sourceDirectoryPaths.set(
                provider {
                    listOf(
                        layout.buildDirectory.dir("outputs/bundle/$variantName").get().asFile.absolutePath,
                        layout.buildDirectory.dir("outputs/bundle/online/release").get().asFile.absolutePath,
                    )
                },
            )
            artifactExtension.set("aab")
            outputFileName.set(aabFileName)
        }

        tasks.matching { it.name == "assemble$variantCapitalized" }.configureEach {
            finalizedBy(renameApkTask)
        }
        tasks.matching { it.name == "bundle$variantCapitalized" }.configureEach {
            finalizedBy(renameBundleTask)
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
    implementation(libs.androidx.compose.material.icons.extended)
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
