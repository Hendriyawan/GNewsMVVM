package com.hdev.gnews.data.source.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hdev.gnews.data.source.local.room.dao.NewsCacheEntity
import com.hdev.gnews.data.source.local.room.dao.NewsDao
import com.hdev.gnews.data.source.local.room.entity.NewsEntity

@Database(entities = [NewsEntity::class, NewsCacheEntity::class], version = 2, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}
