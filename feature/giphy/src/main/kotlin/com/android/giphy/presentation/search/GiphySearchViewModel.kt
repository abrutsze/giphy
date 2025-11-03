package com.android.giphy.presentation.search

import androidx.lifecycle.viewModelScope
import com.android.giphy.domain.usecase.GetTrendingGifsUseCase
import com.android.giphy.domain.usecase.SearchGifsUseCase
import com.android.giphy.presentation.search.mvi.GiphySearchAction
import com.android.giphy.presentation.search.mvi.GiphySearchEffect
import com.android.giphy.presentation.search.mvi.GiphySearchIntent
import com.android.giphy.presentation.search.mvi.GiphySearchReducer
import com.android.giphy.presentation.search.mvi.GiphySearchState
import com.android.mvi.MviBaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@OptIn(FlowPreview::class)
@KoinViewModel
class GiphySearchViewModel(
    private val searchGifsUseCase: SearchGifsUseCase,
    private val getTrendingGifsUseCase: GetTrendingGifsUseCase,
    reducer: GiphySearchReducer
) : MviBaseViewModel<GiphySearchState, GiphySearchAction, GiphySearchIntent>(
    initialState = GiphySearchState(),
    reducer = reducer
) {

    private val searchQueryFlow = MutableSharedFlow<String>(replay = 1)

    companion object {
        private const val PAGE_SIZE = 25
        private const val SEARCH_DEBOUNCE_MS = 500L
    }

    init {
        // Load trending GIFs on start
        loadTrendingGifs()

        // Set up debounced search
        searchQueryFlow
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { query ->
                performSearch(query, isNewSearch = true)
            }
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: GiphySearchIntent) {
        when (intent) {
            is GiphySearchIntent.OnSearchQueryChanged -> {
                onAction(GiphySearchAction.UpdateSearchQuery(intent.query))
                if (intent.query.isBlank()) {
                    // If search is cleared, reload trending
                    loadTrendingGifs()
                } else {
                    // Emit to debounced flow
                    viewModelScope.launch {
                        searchQueryFlow.emit(intent.query)
                    }
                }
            }

            is GiphySearchIntent.OnLoadTrending -> {
                loadTrendingGifs()
            }

            is GiphySearchIntent.OnLoadMore -> {
                if (viewState.hasMorePages && !viewState.isLoadingMore && !viewState.isLoading) {
                    if (viewState.isSearchMode) {
                        performSearch(viewState.searchQuery, isNewSearch = false)
                    } else {
                        loadTrendingGifs(isLoadMore = true)
                    }
                }
            }

            is GiphySearchIntent.OnRetry -> {
                if (viewState.isSearchMode) {
                    performSearch(viewState.searchQuery, isNewSearch = true)
                } else {
                    loadTrendingGifs()
                }
            }

            is GiphySearchIntent.OnGifClicked -> {
                onEffect(GiphySearchEffect.NavigateToDetail(intent.gifId))
            }
        }
    }

    private fun loadTrendingGifs(isLoadMore: Boolean = false) {
        viewModelScope.launch {
            if (isLoadMore) {
                onAction(GiphySearchAction.LoadingMoreStarted)
            } else {
                onAction(GiphySearchAction.LoadingStarted)
            }

            val offset = if (isLoadMore) viewState.currentPage * PAGE_SIZE else 0

            getTrendingGifsUseCase(PAGE_SIZE, offset)
                .onSuccess { gifs ->
                    onAction(
                        GiphySearchAction.GifsLoaded(
                            gifs = gifs,
                            hasMore = gifs.size >= PAGE_SIZE,
                            isNewSearch = !isLoadMore
                        )
                    )
                }
                .onFailure { error ->
                    onAction(
                        GiphySearchAction.LoadingFailed(
                            error.message ?: "Failed to load trending GIFs"
                        )
                    )
                }
        }
    }

    private fun performSearch(query: String, isNewSearch: Boolean) {
        viewModelScope.launch {
            if (isNewSearch) {
                onAction(GiphySearchAction.LoadingStarted)
            } else {
                onAction(GiphySearchAction.LoadingMoreStarted)
            }

            val offset = if (isNewSearch) 0 else viewState.currentPage * PAGE_SIZE

            searchGifsUseCase(query, PAGE_SIZE, offset)
                .onSuccess { gifs ->
                    onAction(
                        GiphySearchAction.GifsLoaded(
                            gifs = gifs,
                            hasMore = gifs.size >= PAGE_SIZE,
                            isNewSearch = isNewSearch
                        )
                    )
                }
                .onFailure { error ->
                    onAction(
                        GiphySearchAction.LoadingFailed(
                            error.message ?: "Search failed"
                        )
                    )
                }
        }
    }
}