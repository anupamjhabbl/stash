package com.example.stash.domain.model.dto

import com.example.stash.domain.model.entity.CategoryWithItems
import com.example.stash.domain.model.entity.StashCategory
import com.example.stash.domain.model.entity.StashItem

object EntityToDtoMapper {
    fun StashCategory.mapToDto(): com.example.stash.domain.model.dto.StashCategory {
        return com.example.stash.domain.model.dto.StashCategory(
            categoryId = categoryId,
            categoryName = categoryName
        )
    }

    fun StashItem.mapToDto(): com.example.stash.domain.model.dto.StashItem {
        return com.example.stash.domain.model.dto.StashItem(
            stashItemId = stashItemId,
            stashCategoryId = stashCategoryId,
            stashItemName = stashItemName,
            stashItemUrl = stashItemUrl,
            stashItemRating = stashItemRating,
            stashItemCompleted = stashItemCompleted
        )
    }

    fun CategoryWithItems.mapToDto(): StashCategoryWithItem {
        return StashCategoryWithItem(
            stashCategory = category.mapToDto(),
            stashItems = items.map { it.mapToDto() }
        )
    }
}