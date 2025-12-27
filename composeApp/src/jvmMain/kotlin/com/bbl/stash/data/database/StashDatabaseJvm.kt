package com.bbl.stash.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<StashDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "stash.db")
    return Room.databaseBuilder<StashDatabase>(
        name = dbFile.absolutePath,
    ).setDriver(BundledSQLiteDriver())
}
