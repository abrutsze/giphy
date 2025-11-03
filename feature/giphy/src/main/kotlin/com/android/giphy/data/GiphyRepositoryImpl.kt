package com.android.giphy.data

import com.android.dispatchers.api.DispatchersProvider
import com.android.giphy.domain.mapper.toUiGif
import com.android.giphy.domain.mapper.toUiGifs
import com.android.models.UiGif
import com.android.network.api.GiphyApi
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class GiphyRepositoryImpl(
    private val giphyApi: GiphyApi,
    private val dispatchersProvider: DispatchersProvider
) : GiphyRepository {

    override suspend fun searchGifs(query: String, limit: Int, offset: Int): Result<List<UiGif>> =
        withContext(dispatchersProvider.io) {
            giphyApi.searchGifs(query, limit, offset).mapCatching { response ->
                response.data.toUiGifs()
            }
        }

    override suspend fun getTrendingGifs(limit: Int, offset: Int): Result<List<UiGif>> =
        withContext(dispatchersProvider.io) {
            giphyApi.getTrendingGifs(limit, offset).mapCatching { response ->
                response.data.toUiGifs()
            }
        }

    override suspend fun getGifById(gifId: String): Result<UiGif> =
        withContext(dispatchersProvider.io) {
            giphyApi.getGifById(gifId).mapCatching { response ->
                response.data.toUiGif()
            }
        }
}