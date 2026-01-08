package com.bbl.stash.domain.usecase

import com.bbl.stash.domain.model.dto.StashCategoryWithItem
import com.bbl.stash.domain.model.dto.StashItem
import com.bbl.stash.domain.repository.StashDataRepository
import kotlinx.coroutines.flow.Flow

class StashDataUseCase(
    private val stashDataRepository: StashDataRepository,
) {
    fun getCategoryDataWithItems(loggedUserId: String): Flow<List<StashCategoryWithItem>> {
        return stashDataRepository.getCategoryDataWithItems(loggedUserId)
    }

    suspend fun addStashCategory(categoryId: String?, categoryName: String, loggedUserId: String) {
        stashDataRepository.addStashCategory(categoryId, categoryName, loggedUserId)
    }

    fun getCategoryDataWithItemsForId(stashCategoryId: String, loggedUserId: String): Flow<StashCategoryWithItem>{
        return stashDataRepository.getCategoryDataWithItemsForId(stashCategoryId, loggedUserId)
    }

    suspend fun addStashItem(
        loggedUserId: String,
        stashItemId: String?,
        stashCategoryId: String,
        stashItemName: String,
        stashItemUrl: String,
        stashItemRating: Float,
        itemCompletedStatus: String
    ) {
        stashDataRepository.addStashItem(
            loggedUserId,
            stashItemId,
            stashCategoryId,
            stashItemName,
            stashItemUrl,
            stashItemRating,
            itemCompletedStatus
        )
    }

    suspend fun deleteStashCategory(categoryId: String, loggedUserId: String) {
        stashDataRepository.deleteStashCategory(categoryId, loggedUserId)
    }

    suspend fun deleteStashItem(itemId: String, loggedUserId: String) {
        stashDataRepository.deleteStashItem(itemId, loggedUserId)
    }
}