package com.bbl.stash.di

import com.bbl.stash.data.dao.StashDao
import com.bbl.stash.data.database.getDatabaseBuilder
import com.bbl.stash.data.database.getRoomDatabase
import org.koin.dsl.module

actual val databaseModule = module {
    single<StashDao> {
        getRoomDatabase(getDatabaseBuilder()).getDao()
    }
}