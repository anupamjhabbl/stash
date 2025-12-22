package com.example.stash.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stash.domain.model.entity.CategoryWithItems
import com.example.stash.domain.model.entity.CategoryWithSync
import com.example.stash.domain.model.entity.StashCategory
import com.example.stash.domain.model.entity.StashCategorySync
import com.example.stash.domain.model.entity.StashItem
import com.example.stash.domain.model.entity.StashItemSync
import com.example.stash.domain.model.entity.StashItemWithSync
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
    @Query("SELECT * FROM stash_category")
    fun getCategoriesWithItems(): Flow<List<CategoryWithItems>>

    @Transaction
    @Query("SELECT * FROM stash_category WHERE categoryId = :categoryId")
    fun getCategoryWithItems(categoryId: String): Flow<CategoryWithItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashCategorySync(stashCategorySync: StashCategorySync)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashCategorySyncList(stashCategorySyncList: List<StashCategorySync>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashItemSync(stashItemSync: StashItemSync)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashItemSyncList(stashItemSyncList: List<StashItemSync>)

    @Transaction
    @Query("SELECT * FROM stash_category")
    suspend fun getCategoriesWithSyncData(): List<CategoryWithSync>

    @Transaction
    @Query("SELECT * FROM stash_item")
    suspend fun getItemsWithSyncData(): List<StashItemWithSync>
}