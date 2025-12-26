package com.example.stash.domain.repository

import com.example.stash.domain.model.dto.StashCategoryWithItem
import kotlinx.coroutines.flow.Flow

interface StashDataRepository {
    fun getCategoryDataWithItems(loggedUserId: String): Flow<List<StashCategoryWithItem>>
    suspend fun addStashCategory(categoryName: String, loggedUserId: String)
    fun getCategoryDataWithItemsForId(stashCategoryId: String, loggedUserId: String): Flow<StashCategoryWithItem>
    suspend fun addStashItem(
        loggedUserId: String,
        stashItemId: String?,
        stashCategoryId: String,
        stashItemName: String,
        stashItemUrl: String,
        stashItemRating: Float,
        itemCompletedStatus: String
    )
}