plugins {
    id("delta.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.delta.core.designsystem"

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
}
