package com.android.giphy.presentation.detail.mvi

import com.android.mvi.MviIntent

sealed interface GiphyDetailIntent : MviIntent {
    data class LoadGif(val gifId: String) : GiphyDetailIntent
    data object Retry : GiphyDetailIntent
}