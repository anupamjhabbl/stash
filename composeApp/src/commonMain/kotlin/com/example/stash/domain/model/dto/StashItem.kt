package com.example.stash.domain.model.dto

import com.example.stash.domain.model.entity.StashItemCategoryStatus

data class StashItem(
    val stashItemId: Long,
    val stashItemName: String = "",
    val stashItemUrl: String = "",
    val stashItemRating: Float = 0f,
    val stashItemCompleted: String = StashItemCategoryStatus.NOT_STARTED.status
)
