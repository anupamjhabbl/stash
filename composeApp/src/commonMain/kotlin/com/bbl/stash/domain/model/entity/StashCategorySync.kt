package com.bbl.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.bbl.stash.common.UUIDUtils
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
    @PrimaryKey
    val stashCategorySyncId: String = UUIDUtils.generateUUID(),
    val stashCategoryId: String,
    val syncStatus: String,
    val lastUpdated: Long = Clock.System.now().toEpochMilliseconds()
)