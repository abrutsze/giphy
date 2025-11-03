package com.android.network.api

import com.android.response.GiphyResponse
import com.android.response.SingleGifResponse

interface GiphyApi {
    suspend fun searchGifs(query: String, limit: Int, offset: Int): Result<GiphyResponse>
    suspend fun getTrendingGifs(limit: Int, offset: Int): Result<GiphyResponse>
    suspend fun getGifById(gifId: String): Result<SingleGifResponse>
}