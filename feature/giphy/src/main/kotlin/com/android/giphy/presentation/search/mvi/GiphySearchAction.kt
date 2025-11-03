package com.android.giphy.presentation.search.mvi

import com.android.models.UiGif
import com.android.mvi.MviAction

sealed interface GiphySearchAction : MviAction {
    data class UpdateSearchQuery(val query: String) : GiphySearchAction
    data object LoadingStarted : GiphySearchAction
    data object LoadingMoreStarted : GiphySearchAction
    data class GifsLoaded(val gifs: List<UiGif>, val hasMore: Boolean, val isNewSearch: Boolean) : GiphySearchAction
    data class LoadingFailed(val error: String) : GiphySearchAction
    data object ClearError : GiphySearchAction
}