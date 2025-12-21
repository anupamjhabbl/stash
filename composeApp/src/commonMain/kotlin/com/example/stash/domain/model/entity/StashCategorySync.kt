package com.example.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(
    tableName = "stash_category_sync",
    foreignKeys = [
        ForeignKey(
            entity = StashCategory::class,
            parentColumns = ["categoryId"],
            childColumns = ["stashCategoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class StashCategorySync @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val stashCategoryId: Long,
    val syncStatus: String,
    val lastUpdated: Long = Clock.System.now().toEpochMilliseconds()
)