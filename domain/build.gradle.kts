plugins {
    id("delta.android.library")
}

android {
    namespace = "com.delta.domain"
}

dependencies {
    implementation(project(":core:core-common"))
}
