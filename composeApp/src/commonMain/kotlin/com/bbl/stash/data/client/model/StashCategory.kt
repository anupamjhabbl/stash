package com.bbl.stash.data.client.model

import com.bbl.stash.domain.model.dto.StashCategory
import kotlinx.serialization.Serializable

@Serializable
data class StashCategory(
    val stashCategory: StashCategory,
    val lastUpdated: Long
)

@Serializable
data class StashCategoryBatch(
    val stashCategoryList: List<com.bbl.stash.data.client.model.StashCategory>,
)

@Serializable
data class StashDeleteCategories(
    val deleteCategoryList: List<String>
)