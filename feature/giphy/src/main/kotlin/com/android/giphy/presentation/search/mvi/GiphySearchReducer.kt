package com.android.giphy.presentation.search.mvi

import com.android.mvi.Reducer
import org.koin.core.annotation.Single

@Single
class GiphySearchReducer : Reducer<GiphySearchAction, GiphySearchState> {
    override fun reduce(action: GiphySearchAction, state: GiphySearchState): GiphySearchState {
        return when (action) {
            is GiphySearchAction.UpdateSearchQuery -> state.copy(
                searchQuery = action.query,
                isSearchMode = action.query.isNotBlank()
            )

            is GiphySearchAction.LoadingStarted -> state.copy(
                isLoading = true,
                error = null
            )

            is GiphySearchAction.LoadingMoreStarted -> state.copy(
                isLoadingMore = true,
                error = null
            )

            is GiphySearchAction.GifsLoaded -> {
                if (action.isNewSearch) {
                    state.copy(
                        gifs = action.gifs,
                        isLoading = false,
                        isLoadingMore = false,
                        error = null,
                        currentPage = 1,
                        hasMorePages = action.hasMore
                    )
                } else {
                    state.copy(
                        gifs = state.gifs + action.gifs,
                        isLoading = false,
                        isLoadingMore = false,
                        error = null,
                        currentPage = state.currentPage + 1,
                        hasMorePages = action.hasMore
                    )
                }
            }

            is GiphySearchAction.LoadingFailed -> state.copy(
                isLoading = false,
                isLoadingMore = false,
                error = action.error
            )

            is GiphySearchAction.ClearError -> state.copy(
                error = null
            )
        }
    }
}