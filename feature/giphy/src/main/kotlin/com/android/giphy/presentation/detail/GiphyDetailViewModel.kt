package com.android.giphy.presentation.detail

import androidx.lifecycle.viewModelScope
import com.android.giphy.domain.usecase.GetGifByIdUseCase
import com.android.giphy.presentation.detail.mvi.GiphyDetailAction
import com.android.giphy.presentation.detail.mvi.GiphyDetailEffect
import com.android.giphy.presentation.detail.mvi.GiphyDetailIntent
import com.android.giphy.presentation.detail.mvi.GiphyDetailReducer
import com.android.giphy.presentation.detail.mvi.GiphyDetailState
import com.android.mvi.MviBaseViewModel
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class GiphyDetailViewModel(
    private val getGifByIdUseCase: GetGifByIdUseCase,
    reducer: GiphyDetailReducer
) : MviBaseViewModel<GiphyDetailState, GiphyDetailAction, GiphyDetailIntent>(
    initialState = GiphyDetailState(),
    reducer = reducer
) {

    private var currentGifId: String? = null

    override fun handleIntent(intent: GiphyDetailIntent) {
        when (intent) {
            is GiphyDetailIntent.LoadGif -> loadGif(intent.gifId)
            is GiphyDetailIntent.Retry -> currentGifId?.let { loadGif(it) }
        }
    }

    private fun loadGif(gifId: String) {
        currentGifId = gifId
        viewModelScope.launch {
            onAction(GiphyDetailAction.LoadingStarted)

            getGifByIdUseCase(gifId)
                .onSuccess { gif ->
                    onAction(GiphyDetailAction.GifLoaded(gif))
                }
                .onFailure { error ->
                    onAction(
                        GiphyDetailAction.LoadingFailed(
                            error.message ?: "Failed to load GIF"
                        )
                    )
                }
        }
    }
}