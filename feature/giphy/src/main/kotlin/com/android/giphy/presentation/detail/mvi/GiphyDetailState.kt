package com.android.giphy.presentation.detail.mvi

import com.android.models.UiGif
import com.android.mvi.MviState

data class GiphyDetailState(
    val gif: UiGif? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : MviState