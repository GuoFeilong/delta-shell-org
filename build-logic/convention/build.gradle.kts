plugins {
    `kotlin-dsl`
}

group = "com.delta.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "delta.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidApplication") {
            id = "delta.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("hiltAndroidLibrary") {
            id = "delta.android.hilt.library"
            implementationClass = "HiltAndroidLibraryConventionPlugin"
        }
        register("hiltAndroidApplication") {
            id = "delta.android.hilt.application"
            implementationClass = "HiltAndroidApplicationConventionPlugin"
        }
    }
}
