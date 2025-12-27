package com.bbl.stash.domain.usecase

import com.bbl.stash.domain.model.dto.StashCategoryWithItem
import com.bbl.stash.domain.repository.StashDataRepository
import kotlinx.coroutines.flow.Flow

class StashDataUseCase(
    private val stashDataRepository: StashDataRepository,
) {
    fun getCategoryDataWithItems(loggedUserId: String): Flow<List<StashCategoryWithItem>> {
        return stashDataRepository.getCategoryDataWithItems(loggedUserId)
    }

    suspend fun addStashCategory(categoryName: String, loggedUserId: String) {
        stashDataRepository.addStashCategory(categoryName, loggedUserId)
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
}