package com.bbl.stash.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<StashDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("stash.db")
    return Room.databaseBuilder<StashDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
