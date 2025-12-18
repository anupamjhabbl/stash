package com.example.stash.domain.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithItems(
    @Embedded val category: StashCategory,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "stashCategoryId"
    )
    val items: List<StashItem>
)
