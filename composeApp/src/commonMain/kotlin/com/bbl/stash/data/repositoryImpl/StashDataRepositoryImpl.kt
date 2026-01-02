package com.bbl.stash.data.repositoryImpl

import com.bbl.stash.data.dao.StashDao
import com.bbl.stash.domain.model.dto.EntityToDtoMapper.mapToDto
import com.bbl.stash.domain.model.dto.StashCategoryWithItem
import com.bbl.stash.domain.model.entity.DeletedCategory
import com.bbl.stash.domain.model.entity.DeletedItem
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
        stashDao.insertStashItem(stashItem)
        stashDao.insertStashItemSync(
            StashItemSync(
                stashItemId = stashItem.stashItemId,
                syncStatus = SyncStatus.PENDING.status
            )
        )
    }

    override suspend fun deleteStashCategory(categoryId: String) {
        stashDao.deleteStashCategory(categoryId)
        stashDao.insertDeletedCategory(DeletedCategory(categoryId))
    }

    override suspend fun deleteStashItem(itemId: String) {
        stashDao.deleteStashItem(itemId)
        stashDao.insertDeletedItem(DeletedItem(itemId))
    }
}