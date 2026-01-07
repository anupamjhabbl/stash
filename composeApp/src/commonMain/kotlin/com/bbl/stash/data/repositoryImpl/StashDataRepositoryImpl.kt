package com.bbl.stash.data.repositoryImpl

import com.bbl.stash.data.dao.StashDao
import com.bbl.stash.domain.model.dto.EntityToDtoMapper.mapToDto
import com.bbl.stash.domain.model.dto.StashCategoryWithItem
import com.bbl.stash.domain.model.entity.StashCategory
import com.bbl.stash.domain.repository.StashDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StashDataRepositoryImpl(
    private val stashDao: StashDao
): StashDataRepository {
    override fun getCategoryDataWithItems(loggedUserId: String): Flow<List<StashCategoryWithItem>> {
        return stashDao.getCategoriesWithItems(loggedUserId).map { stashCategoryList ->
            stashCategoryList.map { stashCategory ->
                stashCategory.mapToDto()
            }.reversed()
        }
    }

    override suspend fun addStashCategory(categoryId: String?, categoryName: String, loggedUserId: String) {
        val stashCategory = if (categoryId == null) {
            StashCategory(
                userId = loggedUserId,
                categoryName = categoryName
            )
        } else {
            StashCategory(
                categoryId = categoryId,
                userId = loggedUserId,
                categoryName = categoryName
            )
        }
        stashDao.insertStashCategoryAndSyncStatus(stashCategory)
    }

    override fun getCategoryDataWithItemsForId(stashCategoryId: String, loggedUserId: String): Flow<StashCategoryWithItem> {
        val stashCategoryWithItem = stashDao.getCategoryWithItems(stashCategoryId, loggedUserId)
        return stashCategoryWithItem.map { stashCategory ->
            val dtoStashCategoryWithItem = stashCategory.mapToDto()
            dtoStashCategoryWithItem.copy(stashItems = dtoStashCategoryWithItem.stashItems.reversed())
        }
    }

    override suspend fun addStashItem(
        loggedUserId: String,
        stashItemId: String?,
        stashCategoryId: String,
        stashItemName: String,
        stashItemUrl: String,
        stashItemRating: Float,
        itemCompletedStatus: String
    ) {
        val stashItem = if (stashItemId != null) {
            com.bbl.stash.domain.model.entity.StashItem(
                stashItemId = stashItemId,
                userId = loggedUserId,
                stashCategoryId = stashCategoryId,
                stashItemName = stashItemName,
                stashItemUrl = stashItemUrl,
                stashItemRating = stashItemRating,
                stashItemCompleted = itemCompletedStatus
            )
        } else {
            com.bbl.stash.domain.model.entity.StashItem(
                stashCategoryId = stashCategoryId,
                userId = loggedUserId,
                stashItemName = stashItemName,
                stashItemUrl = stashItemUrl,
                stashItemRating = stashItemRating,
                stashItemCompleted = itemCompletedStatus
            )
        }
        stashDao.insertItemAndSyncStatus(stashItem)
    }

    override suspend fun deleteStashCategory(categoryId: String) {
        stashDao.deleteStashCategoryAndAddInDeleted(categoryId)
    }

    override suspend fun deleteStashItem(itemId: String) {
        stashDao.deleteStashItemAndAddInDeleted(itemId)
    }
}