package com.android.giphy.presentation.detail.mvi

import com.android.mvi.Reducer
import org.koin.core.annotation.Single

@Single
class GiphyDetailReducer : Reducer<GiphyDetailAction, GiphyDetailState> {
    override fun reduce(action: GiphyDetailAction, state: GiphyDetailState): GiphyDetailState {
        return when (action) {
            is GiphyDetailAction.LoadingStarted -> state.copy(
                isLoading = true,
                error = null
            )

            is GiphyDetailAction.GifLoaded -> state.copy(
                gif = action.gif,
                isLoading = false,
                error = null
            )

            is GiphyDetailAction.LoadingFailed -> state.copy(
                isLoading = false,
                error = action.error
            )
        }
    }
}