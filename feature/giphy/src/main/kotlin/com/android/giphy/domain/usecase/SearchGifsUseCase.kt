package com.android.giphy.domain.usecase

import com.android.giphy.data.GiphyRepository
import com.android.models.UiGif
import org.koin.core.annotation.Single

@Single
class SearchGifsUseCase(private val repository: GiphyRepository) {
    suspend operator fun invoke(query: String, limit: Int, offset: Int): Result<List<UiGif>> =
        repository.searchGifs(query, limit, offset)
}