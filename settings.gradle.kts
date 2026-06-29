pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "delta-shell-org"

// Apps
include(":apps:main-app")
include(":apps:delta-helper-app")
include(":apps:lite-app")
include(":apps:tv-app")

// Features
include(":features:feature-login")
include(":features:feature-home")
include(":features:feature-activation")
include(":features:feature-search")

// Core
include(":core:core-network")
include(":core:core-activation")
include(":core:core-database")
include(":core:core-designsystem")
include(":core:core-common")
include(":core:core-model")

// Domain
include(":domain")

// Build Logic (Convention Plugins)
includeBuild("build-logic")
include(":apps:delta-sights-helper")
