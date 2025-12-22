package com.example.stash.domain.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithSync(
    @Embedded
    val stashCategory: StashCategory,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "stashCategoryId"
    )
    val syncData: StashCategorySync
)