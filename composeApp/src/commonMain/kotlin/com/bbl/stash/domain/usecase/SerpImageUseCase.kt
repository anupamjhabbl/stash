package com.bbl.stash.domain.usecase

import com.bbl.stash.domain.repository.SerpImageRepository

class SerpImageUseCase(
    private val serpImageRepository: SerpImageRepository
) {
    suspend fun getImageUri(query: String, apiKey: String): String? {
        return serpImageRepository.getImageUrl(query, apiKey)
    }
}