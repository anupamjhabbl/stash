package com.bbl.stash.data.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerpImage(
    val original: String? = null,
    @SerialName("original_height")
    val height: Int? = null,
    @SerialName("original_width")
    val width: Int? = null
)

@Serializable
data class SerpApiResult(
    @SerialName("images_results")
    val imagesResults: List<SerpImage> = emptyList()
)