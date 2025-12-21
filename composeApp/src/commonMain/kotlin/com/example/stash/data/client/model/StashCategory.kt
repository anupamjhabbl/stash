package com.example.stash.data.client.model

import com.example.stash.domain.model.dto.StashCategory
import kotlinx.serialization.Serializable

@Serializable
data class StashCategory(
    val stashCategory: StashCategory,
    val lastUpdated: Long
)

@Serializable
data class StashCategoryBatch(
    val stashCategoryList: List<com.example.stash.data.client.model.StashCategory>,
)