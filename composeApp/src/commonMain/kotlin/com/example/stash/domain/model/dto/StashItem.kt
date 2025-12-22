package com.example.stash.domain.model.dto

import com.example.stash.domain.model.entity.StashItemCategoryStatus
import kotlinx.serialization.Serializable

@Serializable
data class StashItem(
    val stashItemId: Long,
    val stashCategoryId: Long,
    val stashItemName: String = "",
    val stashItemUrl: String = "",
    val stashItemRating: Float = 0f,
    val stashItemCompleted: String = StashItemCategoryStatus.NOT_STARTED.status
)
