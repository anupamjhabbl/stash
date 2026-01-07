package com.bbl.stash.data.repositoryImpl

import com.bbl.stash.data.client.SerpImageClient
import com.bbl.stash.domain.repository.SerpImageRepository

class SerpImageNetwork(
    private val serpImageClient: SerpImageClient
): SerpImageRepository {
    override suspend fun getImageUrl(query: String, apiKey: String): String? {
        val result = serpImageClient.getImageUri(GOOGLE_IMAGE_ENGINE, query, apiKey)
        val image = result
            .imagesResults
            .firstOrNull {
                it.height != null && it.width != null && it.height == it.width
            } ?: result.imagesResults.firstOrNull()

        return image?.original

    }

    companion object {
        private const val GOOGLE_IMAGE_ENGINE = "google_images"
    }
}