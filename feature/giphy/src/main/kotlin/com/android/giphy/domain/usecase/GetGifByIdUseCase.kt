package com.android.giphy.domain.usecase

import com.android.giphy.data.GiphyRepository
import com.android.models.UiGif
import org.koin.core.annotation.Factory

@Factory
class GetGifByIdUseCase(
    private val repository: GiphyRepository
) {
    suspend operator fun invoke(gifId: String): Result<UiGif> {
        return repository.getGifById(gifId)
    }
}