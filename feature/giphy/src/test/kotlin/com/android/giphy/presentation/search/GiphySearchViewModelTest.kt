package com.android.giphy.presentation.search

import com.android.giphy.domain.usecase.GetTrendingGifsUseCase
import com.android.giphy.domain.usecase.SearchGifsUseCase
import com.android.giphy.presentation.search.mvi.GiphySearchAction
import com.android.giphy.presentation.search.mvi.GiphySearchIntent
import com.android.giphy.presentation.search.mvi.GiphySearchReducer
import com.android.giphy.presentation.search.mvi.GiphySearchState
import com.android.models.UiGif
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GiphySearchViewModelTest {

    private lateinit var searchGifsUseCase: SearchGifsUseCase
    private lateinit var getTrendingGifsUseCase: GetTrendingGifsUseCase
    private lateinit var reducer: GiphySearchReducer
    private lateinit var viewModel: GiphySearchViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchGifsUseCase = mockk()
        getTrendingGifsUseCase = mockk()
        reducer = GiphySearchReducer()
    }

    @Test
    fun `initial state loads trending gifs`() = runTest(testDispatcher) {
        // Given
        val mockGifs = createMockGifs(5)
        coEvery { getTrendingGifsUseCase(any(), any()) } returns Result.success(mockGifs)

        // When
        viewModel = GiphySearchViewModel(searchGifsUseCase, getTrendingGifsUseCase, reducer)
        advanceUntilIdle()

        // Then
        assertEquals(5, viewModel.viewState.gifs.size)
        assertFalse(viewModel.viewState.isLoading)
        assertFalse(viewModel.viewState.isSearchMode)
        coVerify { getTrendingGifsUseCase(25, 0) }
    }

    @Test
    fun `search query debounces and triggers search after delay`() = runTest(testDispatcher) {
        // Given
        val mockTrendingGifs = createMockGifs(5)
        val mockSearchGifs = createMockGifs(3, prefix = "search")
        coEvery { getTrendingGifsUseCase(any(), any()) } returns Result.success(mockTrendingGifs)
        coEvery { searchGifsUseCase(any(), any(), any()) } returns Result.success(mockSearchGifs)

        viewModel = GiphySearchViewModel(searchGifsUseCase, getTrendingGifsUseCase, reducer)
        advanceUntilIdle()

        // When
        viewModel.onIntent(GiphySearchIntent.OnSearchQueryChanged("funny"))
        advanceTimeBy(300) // Less than debounce time
        viewModel.onIntent(GiphySearchIntent.OnSearchQueryChanged("funny cats"))
        advanceTimeBy(500) // Debounce time
        advanceUntilIdle()

        // Then
        assertEquals("funny cats", viewModel.viewState.searchQuery)
        assertTrue(viewModel.viewState.isSearchMode)
        assertEquals(3, viewModel.viewState.gifs.size)
        coVerify(exactly = 1) { searchGifsUseCase("funny cats", 25, 0) }
        coVerify(exactly = 0) { searchGifsUseCase("funny", 25, 0) } // Should not search for incomplete query
    }

    @Test
    fun `clear search query loads trending gifs`() = runTest(testDispatcher) {
        // Given
        val mockTrendingGifs = createMockGifs(5)
        val mockSearchGifs = createMockGifs(3)
        coEvery { getTrendingGifsUseCase(any(), any()) } returns Result.success(mockTrendingGifs)
        coEvery { searchGifsUseCase(any(), any(), any()) } returns Result.success(mockSearchGifs)

        viewModel = GiphySearchViewModel(searchGifsUseCase, getTrendingGifsUseCase, reducer)
        advanceUntilIdle()

        viewModel.onIntent(GiphySearchIntent.OnSearchQueryChanged("test"))
        advanceTimeBy(500)
        advanceUntilIdle()

        // When
        viewModel.onIntent(GiphySearchIntent.OnSearchQueryChanged(""))
        advanceUntilIdle()

        // Then
        assertEquals("", viewModel.viewState.searchQuery)
        assertFalse(viewModel.viewState.isSearchMode)
        coVerify(atLeast = 2) { getTrendingGifsUseCase(25, 0) } // Once on init, once after clear
    }

    @Test
    fun `load more appends gifs to existing list`() = runTest(testDispatcher) {
        // Given
        val mockGifs1 = createMockGifs(25, prefix = "page1")
        val mockGifs2 = createMockGifs(25, prefix = "page2")
        coEvery { getTrendingGifsUseCase(25, 0) } returns Result.success(mockGifs1)
        coEvery { getTrendingGifsUseCase(25, 25) } returns Result.success(mockGifs2)

        viewModel = GiphySearchViewModel(searchGifsUseCase, getTrendingGifsUseCase, reducer)
        advanceUntilIdle()

        // When
        viewModel.onIntent(GiphySearchIntent.OnLoadMore)
        advanceUntilIdle()

        // Then
        assertEquals(50, viewModel.viewState.gifs.size)
        assertEquals(2, viewModel.viewState.currentPage)
        assertFalse(viewModel.viewState.isLoadingMore)
        coVerify { getTrendingGifsUseCase(25, 25) }
    }

    @Test
    fun `error state is set when API fails`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Network error"
        coEvery { getTrendingGifsUseCase(any(), any()) } returns Result.failure(RuntimeException(errorMessage))

        // When
        viewModel = GiphySearchViewModel(searchGifsUseCase, getTrendingGifsUseCase, reducer)
        advanceUntilIdle()

        // Then
        assertEquals(errorMessage, viewModel.viewState.error)
        assertFalse(viewModel.viewState.isLoading)
        assertEquals(0, viewModel.viewState.gifs.size)
    }

    @Test
    fun `retry reloads data based on current mode`() = runTest(testDispatcher) {
        // Given
        val mockGifs = createMockGifs(5)
        coEvery { getTrendingGifsUseCase(any(), any()) } returns
            Result.failure(RuntimeException("Error")) andThen
            Result.success(mockGifs)

        viewModel = GiphySearchViewModel(searchGifsUseCase, getTrendingGifsUseCase, reducer)
        advanceUntilIdle()

        // When
        viewModel.onIntent(GiphySearchIntent.OnRetry)
        advanceUntilIdle()

        // Then
        assertEquals(5, viewModel.viewState.gifs.size)
        assertEquals(null, viewModel.viewState.error)
        coVerify(exactly = 2) { getTrendingGifsUseCase(25, 0) }
    }

    private fun createMockGifs(count: Int, prefix: String = "gif"): List<UiGif> {
        return (1..count).map {
            UiGif(
                id = "$prefix-$it",
                title = "Test GIF $it",
                thumbnailUrl = "https://example.com/thumb$it.gif",
                originalUrl = "https://example.com/original$it.gif",
                previewUrl = "https://example.com/preview$it.gif",
                rating = "G",
                username = "user$it",
                userAvatar = "",
                width = 200,
                height = 200
            )
        }
    }
}