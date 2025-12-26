package com.example.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.stash.common.UUIDUtils

@Entity(
    tableName = "stash_item",
    foreignKeys = [
        ForeignKey(
            entity = StashCategory::class,
            parentColumns = ["categoryId"],
            childColumns = ["stashCategoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stashCategoryId")]
)
data class StashItem(
    @PrimaryKey
    val stashItemId: String = UUIDUtils.generateUUID(),
    val stashCategoryId: String,
    val userId: String,
    val stashItemName: String = "",
    val stashItemUrl: String = "",
    val stashItemRating: Float = 0f,
    val stashItemCompleted: String = StashItemCategoryStatus.NOT_STARTED.status
)

enum class StashItemCategoryStatus(val status: String) {
    NOT_STARTED("not_started"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed")
}