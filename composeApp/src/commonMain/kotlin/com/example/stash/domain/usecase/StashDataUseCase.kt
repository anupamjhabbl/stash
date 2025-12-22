package com.example.stash.domain.usecase

import com.example.stash.domain.model.dto.StashCategoryWithItem
import com.example.stash.domain.repository.StashDataRepository
import kotlinx.coroutines.flow.Flow

class StashDataUseCase(
    private val stashDataRepository: StashDataRepository
) {
    fun getCategoryDataWithItems(): Flow<List<StashCategoryWithItem>> {
        return stashDataRepository.getCategoryDataWithItems()
    }

    suspend fun addStashCategory(categoryName: String) {
        stashDataRepository.addStashCategory(categoryName)
    }

    fun getCategoryDataWithItemsForId(stashCategoryId: String): Flow<StashCategoryWithItem>{
        return stashDataRepository.getCategoryDataWithItemsForId(stashCategoryId)
    }

    suspend fun addStashItem(
        stashItemId: String?,
        stashCategoryId: String,
        stashItemName: String,
        stashItemUrl: String,
        stashItemRating: Float,
        itemCompletedStatus: String
    ) {
        stashDataRepository.addStashItem(
            stashItemId,
            stashCategoryId,
            stashItemName,
            stashItemUrl,
            stashItemRating,
            itemCompletedStatus
        )
    }
}