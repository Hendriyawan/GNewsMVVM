package com.hdev.gnews.domain.repository

import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.Article
import com.hdev.gnews.domain.model.news.NewsResult
import com.hdev.gnews.domain.model.news.SourcesResult
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun getTopHeadline(
        country: String = "us",
        category: String? = null,
        page: Int = 1,
        pageSize: Int = 20

    ): Flow<Resource<NewsResult>>

    fun getEverything(
        query: String? = null,
        sources: String? = null,
        sortBy: String? = "publishedAt",
        language: String? = "en",
        page: Int = 1,
        pageSize: Int = 20,
    ): Flow<Resource<NewsResult>>

    fun getSources(
        category: String? = null,
        language: String? = "en",
        country: String? = "us",
    ): Flow<Resource<SourcesResult>>

    // Favorite News
    fun getFavoriteNews(): Flow<List<Article>>
    suspend fun saveFavoriteNews(article: Article)
    suspend fun deleteFavoriteNews(article: Article)
    fun isFavoriteNews(url: String): Flow<Boolean>

}
