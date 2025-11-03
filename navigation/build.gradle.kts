plugins {
    alias(libs.plugins.project.android.library)
    alias(libs.plugins.project.android.library.compose)

}

android {
    namespace = "com.android.navigation"
}

dependencies {
    implementation(projects.feature.giphy)
    implementation(projects.screens)
    implementation(libs.androidx.navigation)
}
