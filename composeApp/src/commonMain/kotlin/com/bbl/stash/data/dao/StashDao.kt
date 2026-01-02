package com.bbl.stash.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bbl.stash.domain.model.entity.CategoryWithItems
import com.bbl.stash.domain.model.entity.CategoryWithSync
import com.bbl.stash.domain.model.entity.DeletedCategory
import com.bbl.stash.domain.model.entity.DeletedItem
import com.bbl.stash.domain.model.entity.StashCategory
import com.bbl.stash.domain.model.entity.StashCategorySync
import com.bbl.stash.domain.model.entity.StashItem
import com.bbl.stash.domain.model.entity.StashItemSync
import com.bbl.stash.domain.model.entity.StashItemWithSync
import kotlinx.coroutines.flow.Flow

@Dao
interface StashDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashCategory(stashCategory: StashCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashCategoryList(stashCategoryList: List<StashCategory>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashItem(stashItem: StashItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashItemList(stashItemList: List<StashItem>)

    @Transaction
    @Query("SELECT * FROM stash_category WHERE userId = :loggedUserId")
    fun getCategoriesWithItems(loggedUserId: String): Flow<List<CategoryWithItems>>

    @Transaction
    @Query("SELECT * FROM stash_category WHERE categoryId = :categoryId AND userId = :loggedUserId")
    fun getCategoryWithItems(categoryId: String, loggedUserId: String): Flow<CategoryWithItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashCategorySync(stashCategorySync: StashCategorySync)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashCategorySyncList(stashCategorySyncList: List<StashCategorySync>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashItemSync(stashItemSync: StashItemSync)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashItemSyncList(stashItemSyncList: List<StashItemSync>)

    @Transaction
    @Query("""
        SELECT * FROM stash_category
        WHERE userId = :loggedUserId
        """)
    suspend fun getCategoriesWithSyncData(loggedUserId: String): List<CategoryWithSync>

    @Transaction
    @Query("""
        SELECT * FROM stash_item
        WHERE userId = :loggedUserId
        """)
    suspend fun getItemsWithSyncData(loggedUserId: String): List<StashItemWithSync>

    @Query("""
        DELETE FROM stash_category
        WHERE categoryId = :categoryId
        """)
    suspend fun deleteStashCategory(categoryId: String)

    @Query("""
        DELETE FROM stash_item
        WHERE stashItemId = :itemId
        """)
    suspend fun deleteStashItem(itemId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeletedCategory(deletedCategory: DeletedCategory)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeletedItem(deletedItem: DeletedItem)

    @Query("SELECT * FROM deleted_category")
    suspend fun getDeletedCategory(): List<DeletedCategory>

    @Query("SELECT * FROM deleted_item")
    suspend fun getDeletedItem(): List<DeletedItem>

    @Query("DELETE FROM deleted_category")
    suspend fun clearDeletedCategory()

    @Query("DELETE FROM deleted_item")
    suspend fun clearDeletedItem()
}