package com.example.stash.di

import com.example.stash.data.dao.StashDao
import com.example.stash.data.database.getDatabaseBuilder
import com.example.stash.data.database.getRoomDatabase
import org.koin.dsl.module

actual val databaseModule = module {
    single<StashDao> {
        getRoomDatabase(getDatabaseBuilder()).getDao()
    }
}
