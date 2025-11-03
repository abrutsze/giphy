pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "GiphyCompose"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(
    ":core:network:api",
    ":core:network:impl",
    ":core:dispatchers:api",
    ":core:dispatchers:impl",
    ":core:datastore:api",
    ":core:datastore:impl",
    ":core:ui",
    ":core:resources",
)

include(
    ":common:response",
    ":common:utils",
    ":common:mvi",
    ":common:ui-models",
)

include(
    ":feature:giphy"
)
include(":navigation")
include(":screens")


