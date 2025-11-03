package com.android.giphy.presentation.search.mvi

import com.android.mvi.MviIntent

sealed interface GiphySearchIntent : MviIntent {
    data class OnSearchQueryChanged(val query: String) : GiphySearchIntent
    data object OnLoadTrending : GiphySearchIntent
    data object OnLoadMore : GiphySearchIntent
    data object OnRetry : GiphySearchIntent
    data class OnGifClicked(val gifId: String) : GiphySearchIntent
}