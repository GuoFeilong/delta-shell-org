plugins {
    id("delta.android.library")
}

android {
    namespace = "com.delta.core.network"
}

dependencies {
    implementation(project(":core:core-common"))
}
