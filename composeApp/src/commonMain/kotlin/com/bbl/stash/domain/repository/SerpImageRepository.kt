package com.bbl.stash.domain.repository

interface SerpImageRepository {
    suspend fun getImageUrl(query: String, apiKey: String): String?
}