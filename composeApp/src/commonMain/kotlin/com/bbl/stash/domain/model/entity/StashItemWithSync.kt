package com.bbl.stash.domain.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StashItemWithSync(
    @Embedded
    val stashItem: StashItem,
    @Relation(
        parentColumn = "stashItemId",
        entityColumn = "stashItemId"
    )
    val syncData: StashItemSync
)