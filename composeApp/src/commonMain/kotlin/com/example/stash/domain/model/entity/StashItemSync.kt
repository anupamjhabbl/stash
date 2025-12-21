package com.example.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val stashItemId: Long,
    val syncStatus: String,
    val lastUpdated: Long = Clock.System.now().toEpochMilliseconds()
)
