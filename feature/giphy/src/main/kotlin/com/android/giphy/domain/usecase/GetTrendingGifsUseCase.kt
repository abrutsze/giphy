package com.android.giphy.domain.usecase

import com.android.giphy.data.GiphyRepository
import com.android.models.UiGif
import org.koin.core.annotation.Single

@Single
class GetTrendingGifsUseCase(private val repository: GiphyRepository) {
    suspend operator fun invoke(limit: Int, offset: Int): Result<List<UiGif>> =
        repository.getTrendingGifs(limit, offset)
}