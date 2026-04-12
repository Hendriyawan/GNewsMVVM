package com.hdev.gnews.core.di

import android.content.Context
import androidx.room.Room
import com.hdev.gnews.data.source.local.room.dao.NewsDao
import com.hdev.gnews.data.source.local.room.database.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : NewsDatabase {
        return Room.databaseBuilder(context, NewsDatabase::class.java, "gnews.db").fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideNewsDao(database: NewsDatabase): NewsDao {
        return database.newsDao()
    }

}