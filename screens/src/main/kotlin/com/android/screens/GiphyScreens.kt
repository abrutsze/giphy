package com.android.screens

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
sealed class GiphyScreens : Screens {

    @Serializable
    data object SearchScreen : GiphyScreens()

    @Serializable
    data class DetailScreen(val gifId: String) : GiphyScreens()
}