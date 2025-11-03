package com.android.giphy.data

import com.android.dispatchers.api.DispatchersProvider
import com.android.models.UiGif
import com.android.network.api.GiphyApi
import com.android.response.GifData
import com.android.response.GiphyResponse
import com.android.response.ImageData
import com.android.response.Images
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GiphyRepositoryImplTest {

    private lateinit var giphyApi: GiphyApi
    private lateinit var dispatchersProvider: DispatchersProvider
    private lateinit var repository: GiphyRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        giphyApi = mockk()
        dispatchersProvider = mockk {
            coEvery { io } returns testDispatcher
        }
        repository = GiphyRepositoryImpl(giphyApi, dispatchersProvider)
    }

    @Test
    fun `searchGifs returns success with mapped UiGifs`() = runTest(testDispatcher) {
        // Given
        val query = "funny cats"
        val limit = 25
        val offset = 0
        val mockResponse = createMockGiphyResponse()

        coEvery { giphyApi.searchGifs(query, limit, offset) } returns Result.success(mockResponse)

        // When
        val result = repository.searchGifs(query, limit, offset)

        // Then
        assertTrue(result.isSuccess)
        val gifs = result.getOrNull()
        assertEquals(2, gifs?.size)
        assertEquals("1", gifs?.get(0)?.id)
        assertEquals("Test GIF 1", gifs?.get(0)?.title)

        coVerify { giphyApi.searchGifs(query, limit, offset) }
    }

    @Test
    fun `searchGifs returns failure when API fails`() = runTest(testDispatcher) {
        // Given
        val query = "error"
        val limit = 25
        val offset = 0
        val exception = RuntimeException("Network error")

        coEvery { giphyApi.searchGifs(query, limit, offset) } returns Result.failure(exception)

        // When
        val result = repository.searchGifs(query, limit, offset)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getTrendingGifs returns success with mapped UiGifs`() = runTest(testDispatcher) {
        // Given
        val limit = 25
        val offset = 0
        val mockResponse = createMockGiphyResponse()

        coEvery { giphyApi.getTrendingGifs(limit, offset) } returns Result.success(mockResponse)

        // When
        val result = repository.getTrendingGifs(limit, offset)

        // Then
        assertTrue(result.isSuccess)
        val gifs = result.getOrNull()
        assertEquals(2, gifs?.size)

        coVerify { giphyApi.getTrendingGifs(limit, offset) }
    }

    @Test
    fun `getTrendingGifs returns failure when API fails`() = runTest(testDispatcher) {
        // Given
        val limit = 25
        val offset = 0
        val exception = RuntimeException("API error")

        coEvery { giphyApi.getTrendingGifs(limit, offset) } returns Result.failure(exception)

        // When
        val result = repository.getTrendingGifs(limit, offset)

        // Then
        assertTrue(result.isFailure)
    }

    private fun createMockGiphyResponse(): GiphyResponse {
        val image1 = ImageData(url = "https://example.com/gif1.gif", width = "200", height = "200")
        val image2 = ImageData(url = "https://example.com/gif2.gif", width = "300", height = "300")

        val images1 = Images(original = image1, fixedWidth = image1, fixedHeight = image1)
        val images2 = Images(original = image2, fixedWidth = image2, fixedHeight = image2)

        return GiphyResponse(
            data = listOf(
                GifData(id = "1", title = "Test GIF 1", images = images1, rating = "G"),
                GifData(id = "2", title = "Test GIF 2", images = images2, rating = "PG")
            )
        )
    }
}