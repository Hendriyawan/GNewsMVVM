package com.hdev.gnews.domain.repository

import com.hdev.gnews.data.source.local.room.entity.NewsEntity
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.EverythingResponse
import com.hdev.gnews.domain.model.news.SourcesResponse
import com.hdev.gnews.domain.model.news.TopHeadlineResponse
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    suspend fun getTopHeadline(
        country: String = "us",
        category: String? = null,
        page: Int = 1,
        pageSize: Int = 20

    ): Flow<Resource<TopHeadlineResponse>>

    suspend fun getEverything(
        query: String? = null,
        sources: String? = null,
        sortBy: String? = "publishedAt",
        language: String? = "en",
        page: Int = 1,
        pageSize: Int = 20,
        ): Flow<Resource<EverythingResponse>>

    suspend fun getSources(
        category: String? = null,
        language: String? = "en",
        country: String? = "us",
    ): Flow<Resource<SourcesResponse>>

    // Favorite News
    fun getAllFavoriteNews(): Flow<List<NewsEntity>>
    suspend fun insertFavoriteNews(news: NewsEntity)
    suspend fun deleteFavoriteNews(news: NewsEntity)
    fun isFavoriteNews(url: String): Flow<Boolean>

}