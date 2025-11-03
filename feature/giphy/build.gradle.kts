plugins {
    alias(libs.plugins.project.android.library)
    alias(libs.plugins.project.android.library.compose)
    alias(libs.plugins.project.android.feature)
}

android {
    namespace = "com.android.feature.giphy"
}

dependencies {
    implementation(projects.core.datastore.api)
    testImplementation(kotlin("test"))
}