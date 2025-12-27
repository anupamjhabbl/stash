package com.bbl.stash.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.bbl.stash.data.dao.StashDao
import com.bbl.stash.domain.model.entity.StashCategory
import com.bbl.stash.domain.model.entity.StashCategorySync
import com.bbl.stash.domain.model.entity.StashItem
import com.bbl.stash.domain.model.entity.StashItemSync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [StashCategory::class, StashItem::class, StashItemSync::class, StashCategorySync::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class StashDatabase : RoomDatabase() {
    abstract fun getDao(): StashDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<StashDatabase> {
    override fun initialize(): StashDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<StashDatabase>
): StashDatabase {
    return builder
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}