package com.android.giphy.presentation.detail.mvi

import com.android.models.UiGif
import com.android.mvi.MviAction

sealed interface GiphyDetailAction : MviAction {
    data object LoadingStarted : GiphyDetailAction
    data class GifLoaded(val gif: UiGif) : GiphyDetailAction
    data class LoadingFailed(val error: String) : GiphyDetailAction
}