plugins {
    id("delta.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.delta.core.network"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:core-common"))

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.bundles.test.unit)
    testImplementation(libs.okhttp)
    testImplementation(libs.okhttp.mockwebserver)
}
