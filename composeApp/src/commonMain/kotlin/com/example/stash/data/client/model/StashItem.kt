package com.example.stash.data.client.model

import com.example.stash.domain.model.dto.StashItem
import kotlinx.serialization.Serializable

@Serializable
data class StashItem(
    val stashItem: StashItem,
    val lastUpdated: Long
)

@Serializable
data class StashItemBatch(
    val stashItemList: List<com.example.stash.data.client.model.StashItem>
)