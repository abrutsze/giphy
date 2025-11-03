package com.android.giphy.presentation.search.mvi

import com.android.models.UiGif
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GiphySearchReducerTest {

    private val reducer = GiphySearchReducer()

    @Test
    fun `UpdateSearchQuery updates query and sets search mode`() {
        // Given
        val initialState = GiphySearchState()
        val action = GiphySearchAction.UpdateSearchQuery("funny cats")

        // When
        val newState = reducer.reduce(action, initialState)

        // Then
        assertEquals("funny cats", newState.searchQuery)
        assertTrue(newState.isSearchMode)
    }

    @Test
    fun `UpdateSearchQuery with blank query sets search mode to false`() {
        // Given
        val initialState = GiphySearchState(searchQuery = "test", isSearchMode = true)
        val action = GiphySearchAction.UpdateSearchQuery("")

        // When
        val newState = reducer.reduce(action, initialState)

        // Then
        assertEquals("", newState.searchQuery)
        assertFalse(newState.isSearchMode)
    }

    @Test
    fun `LoadingStarted sets isLoading to true and clears error`() {
        // Given
        val initialState = GiphySearchState(error = "Previous error")
        val action = GiphySearchAction.LoadingStarted

        // When
        val newState = reducer.reduce(action, initialState)

        // Then
        assertTrue(newState.isLoading)
        assertNull(newState.error)
    }

    @Test
    fun `LoadingMoreStarted sets isLoadingMore to true and clears error`() {
        // Given
        val initialState = GiphySearchState(error = "Previous error")
        val action = GiphySearchAction.LoadingMoreStarted

        // When
        val newState = reducer.reduce(action, initialState)

        // Then
        assertTrue(newState.isLoadingMore)
        assertNull(newState.error)
    }

    @Test
    fun `GifsLoaded with isNewSearch replaces gifs list`() {
        // Given
        val existingGifs = createMockGifs(5, "old")
        val initialState = GiphySearchState(gifs = existingGifs, isLoading = true)
        val newGifs = createMockGifs(3, "new")
        val action = GiphySearchAction.GifsLoaded(gifs = newGifs, hasMore = true, isNewSearch = true)

        // When
        val newState = reducer.reduce(action, initialState)

        // Then
        assertEquals(3, newState.gifs.size)
        assertEquals("new-1", newState.gifs[0].id)
        assertEquals(1, newState.currentPage)
        assertTrue(newState.hasMorePages)
        assertFalse(newState.isLoading)
        assertNull(newState.error)
    }

    @Test
    fun `GifsLoaded without isNewSearch appends gifs to list`() {
        // Given
        val existingGifs = createMockGifs(25, "page1")
        val initialState = GiphySearchState(
            gifs = existingGifs,
            currentPage = 1,
            isLoadingMore = true
        )
        val newGifs = createMockGifs(25, "page2")
        val action = GiphySearchAction.GifsLoaded(gifs = newGifs, hasMore = true, isNewSearch = false)

        // When
        val newState = reducer.reduce(action, initialState)

        // Then
        assertEquals(50, newState.gifs.size)
        assertEquals("page1-1", newState.gifs[0].id)
        assertEquals("page2-1", newState.gifs[25].id)
        assertEquals(2, newState.currentPage)
        assertTrue(newState.hasMorePages)
        assertFalse(newState.isLoadingMore)
    }

    @Test
    fun `GifsLoaded sets hasMorePages to false when no more pages`() {
        // Given
        val initialState = GiphySearchState(isLoading = true)
        val gifs = createMockGifs(10, "last")
        val action = GiphySearchAction.GifsLoaded(gifs = gifs, hasMore = false, isNewSearch = true)

        // When
        val newState = reducer.reduce(action, initialState)

        // Then
        assertFalse(newState.hasMorePages)
    }

    @Test
    fun `LoadingFailed sets error and clears loading states`() {
        // Given
        val initialState = GiphySearchState(isLoading = true, isLoadingMore = true)
        val action = GiphySearchAction.LoadingFailed("Network error")

        // When
        val newState = reducer.reduce(action, initialState)

        // Then
        assertEquals("Network error", newState.error)
        assertFalse(newState.isLoading)
        assertFalse(newState.isLoadingMore)
    }

    @Test
    fun `ClearError clears error message`() {
        // Given
        val initialState = GiphySearchState(error = "Some error")
        val action = GiphySearchAction.ClearError

        // When
        val newState = reducer.reduce(action, initialState)

        // Then
        assertNull(newState.error)
    }

    private fun createMockGifs(count: Int, prefix: String): List<UiGif> {
        return (1..count).map {
            UiGif(
                id = "$prefix-$it",
                title = "Test GIF $it",
                thumbnailUrl = "https://example.com/$it.gif",
                originalUrl = "https://example.com/$it.gif",
                previewUrl = "https://example.com/$it.gif",
                rating = "G",
                username = "user",
                userAvatar = "",
                width = 200,
                height = 200
            )
        }
    }
}