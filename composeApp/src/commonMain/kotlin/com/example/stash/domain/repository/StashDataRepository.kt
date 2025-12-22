package com.example.stash.domain.repository

import com.example.stash.domain.model.dto.StashCategoryWithItem
import kotlinx.coroutines.flow.Flow

interface StashDataRepository {
    fun getCategoryDataWithItems(): Flow<List<StashCategoryWithItem>>
    suspend fun addStashCategory(categoryName: String)
    fun getCategoryDataWithItemsForId(stashCategoryId: String): Flow<StashCategoryWithItem>
    suspend fun addStashItem(
        stashItemId: String?,
        stashCategoryId: String,
        stashItemName: String,
        stashItemUrl: String,
        stashItemRating: Float,
        itemCompletedStatus: String
    )
}