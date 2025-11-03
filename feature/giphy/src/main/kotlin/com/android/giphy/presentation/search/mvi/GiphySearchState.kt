package com.android.giphy.presentation.search.mvi

import com.android.models.UiGif
import com.android.mvi.MviState

data class GiphySearchState(
    val searchQuery: String = "",
    val gifs: List<UiGif> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val isSearchMode: Boolean = false
) : MviState