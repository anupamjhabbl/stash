package com.bbl.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.bbl.stash.common.UUIDUtils
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(
    tableName = "stash_item_sync",
    foreignKeys = [
        ForeignKey(
            entity = StashItem::class,
            parentColumns = ["stashItemId"],
            childColumns = ["stashItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StashItemSync @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey
    val stashItemSyncId: String = UUIDUtils.generateUUID(),
    val stashItemId: String,
    val syncStatus: String,
    val lastUpdated: Long = Clock.System.now().toEpochMilliseconds()
)
