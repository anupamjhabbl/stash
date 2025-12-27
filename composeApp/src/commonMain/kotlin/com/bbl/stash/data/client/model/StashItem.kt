package com.bbl.stash.data.client.model

import com.bbl.stash.domain.model.dto.StashItem
import kotlinx.serialization.Serializable

@Serializable
data class StashItem(
    val stashItem: StashItem,
    val lastUpdated: Long
)

@Serializable
data class StashItemBatch(
    val stashItemList: List<com.bbl.stash.data.client.model.StashItem>
)