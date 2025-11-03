package com.android.giphy.presentation.search.mvi

import com.android.mvi.MviEffect

sealed interface GiphySearchEffect : MviEffect {
    data class NavigateToDetail(val gifId: String) : GiphySearchEffect
}