package com.android.network.impl.services.giphy

import com.android.network.api.GiphyApi
import com.android.response.GiphyResponse
import com.android.response.SingleGifResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.request
import org.koin.core.annotation.Single

@Single
class GiphyApiImpl(private val httpClient: HttpClient) : GiphyApi {

    override suspend fun searchGifs(query: String, limit: Int, offset: Int): Result<GiphyResponse> =
        runCatching {
            httpClient.get(SEARCH_URL) {
                parameter("q", query)
                parameter("limit", limit)
                parameter("offset", offset)
            }.body<GiphyResponse>()
        }

    override suspend fun getTrendingGifs(limit: Int, offset: Int): Result<GiphyResponse> =
        runCatching {
            httpClient.get(TRENDING_URL) {
                parameter("limit", limit)
                parameter("offset", offset)
            }.body<GiphyResponse>()
        }

    override suspend fun getGifById(gifId: String): Result<SingleGifResponse> =
        runCatching {
            httpClient.get("$GIF_BY_ID_URL/$gifId").body<SingleGifResponse>()
        }

    companion object {
        private const val SEARCH_URL = "v1/gifs/search"
        private const val TRENDING_URL = "v1/gifs/trending"
        private const val GIF_BY_ID_URL = "v1/gifs"
    }
}