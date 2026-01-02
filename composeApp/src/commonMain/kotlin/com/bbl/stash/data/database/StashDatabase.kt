package com.bbl.stash.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.bbl.stash.data.dao.StashDao
import com.bbl.stash.domain.model.entity.DeletedCategory
import com.bbl.stash.domain.model.entity.DeletedItem
import com.bbl.stash.domain.model.entity.StashCategory
import com.bbl.stash.domain.model.entity.StashCategorySync
import com.bbl.stash.domain.model.entity.StashItem
import com.bbl.stash.domain.model.entity.StashItemSync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [StashCategory::class, StashItem::class, StashItemSync::class, StashCategorySync::class, DeletedItem::class, DeletedCategory::class],
    version = 2
)
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
        .addMigrations(MIGRATION_1_2)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
                CREATE TABLE IF NOT EXISTS deleted_category (
                    categoryId TEXT NOT NULL,
                    PRIMARY KEY(categoryId)
                )
            """.trimIndent()
        )
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS deleted_item (
                itemId TEXT NOT NULL,
                PRIMARY KEY(itemId)
            )
        """.trimIndent())
    }
}