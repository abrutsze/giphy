plugins {
    alias(libs.plugins.project.android.library)
    kotlin("plugin.serialization")
}

android {
    namespace = "com.android.network.response"
}

dependencies {
    // Serialization
    implementation(libs.kotlin.serialization)
}
