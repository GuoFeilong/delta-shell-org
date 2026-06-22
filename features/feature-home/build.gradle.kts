plugins {
    id("delta.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.delta.features.home"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-designsystem"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
}
