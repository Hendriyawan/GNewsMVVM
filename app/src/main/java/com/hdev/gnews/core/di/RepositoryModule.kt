package com.hdev.gnews.core.di

import com.hdev.gnews.data.repository.NewsRepositoryImpl
import com.hdev.gnews.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun NewsRepository(newsRepositoryImpl: NewsRepositoryImpl) : NewsRepository
}