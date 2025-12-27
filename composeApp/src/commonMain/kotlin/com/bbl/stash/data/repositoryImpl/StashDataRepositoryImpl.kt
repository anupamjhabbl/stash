package com.bbl.stash.data.repositoryImpl

import com.bbl.stash.data.dao.StashDao
import com.bbl.stash.domain.model.dto.EntityToDtoMapper.mapToDto
import com.bbl.stash.domain.model.dto.StashCategoryWithItem
import com.bbl.stash.domain.model.entity.StashCategory
import com.bbl.stash.domain.model.entity.StashCategorySync
import com.bbl.stash.domain.model.entity.StashItemSync
import com.bbl.stash.domain.model.entity.SyncStatus
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
            }
        }
    }

    override suspend fun addStashCategory(categoryName: String, loggedUserId: String) {
        val stashCategory = StashCategory(
            userId = loggedUserId,
            categoryName = categoryName
        )
        stashDao.insertStashCategory(stashCategory)
        stashDao.insertStashCategorySync(
            StashCategorySync(
                stashCategoryId = stashCategory.categoryId,
                syncStatus = SyncStatus.PENDING.status
            )
        )
    }

    override fun getCategoryDataWithItemsForId(stashCategoryId: String, loggedUserId: String): Flow<StashCategoryWithItem> {
        val stashCategoryWithItem = stashDao.getCategoryWithItems(stashCategoryId, loggedUserId)
        return stashCategoryWithItem.map { stashCategory ->
            stashCategory.mapToDto()
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
        stashDao.insertStashItem(stashItem)
        stashDao.insertStashItemSync(
            StashItemSync(
                stashItemId = stashItem.stashItemId,
                syncStatus = SyncStatus.PENDING.status
            )
        )
    }
}