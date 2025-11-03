package com.android.giphy.data

import com.android.models.UiGif

interface GiphyRepository {
    suspend fun searchGifs(query: String, limit: Int, offset: Int): Result<List<UiGif>>
    suspend fun getTrendingGifs(limit: Int, offset: Int): Result<List<UiGif>>
    suspend fun getGifById(gifId: String): Result<UiGif>
}