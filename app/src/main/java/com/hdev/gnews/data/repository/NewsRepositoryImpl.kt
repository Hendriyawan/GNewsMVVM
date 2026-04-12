package com.hdev.gnews.data.repository

import com.hdev.gnews.core.utils.NetworkMonitor
import com.hdev.gnews.data.source.local.room.dao.NewsCacheEntity
import com.hdev.gnews.data.source.local.room.dao.NewsDao
import com.hdev.gnews.data.source.local.room.entity.NewsEntity
import com.hdev.gnews.data.source.remote.ApiService
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.ArticlesItem
import com.hdev.gnews.domain.model.news.EverythingResponse
import com.hdev.gnews.domain.model.news.SourcesResponse
import com.hdev.gnews.domain.model.news.TopHeadlineResponse
import com.hdev.gnews.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao,
    private val networkMonitor: NetworkMonitor
) : ResponseHelper(), NewsRepository {


    override suspend fun getTopHeadline(
        country: String,
        category: String?,
        page: Int,
        pageSize: Int
    ): Flow<Resource<TopHeadlineResponse>> = flow {
        emit(Resource.Loading())

        val cacheCategory = category ?: "general"

        if (networkMonitor.isCurrentlyOnline()) {
            val apiResponse = saveApiCall {
                apiService.getTopHeadline(
                    country = country,
                    category = category,
                    page = page,
                    pageSize = pageSize
                )
            }

            apiResponse.collect { resource ->
                if (resource is Resource.Success) {
                    // Save to Cache
                    val cacheList = resource.data?.articles?.filterNotNull()?.map { article ->
                        NewsCacheEntity(
                            url = article.url ?: "",
                            title = article.title,
                            author = article.author,
                            description = article.description,
                            urlToImage = article.urlToImage,
                            publishedAt = article.publishedAt,
                            content = article.content,
                            sourceName = article.source?.name,
                            category = cacheCategory
                        )
                    }
                    if (cacheList != null) {
                        newsDao.deleteNewsCache(cacheCategory)
                        newsDao.insertNewsCache(cacheList)
                    }
                }
                emit(resource)
            }
        } else {
            // Load from Cache
            val localData = newsDao.getNewsCache(cacheCategory)
            if (localData.isNotEmpty()) {
                val articles = localData.map { entity ->
                    ArticlesItem(
                        url = entity.url,
                        title = entity.title,
                        author = entity.author,
                        description = entity.description,
                        urlToImage = entity.urlToImage,
                        publishedAt = entity.publishedAt,
                        content = entity.content,
                        source = com.hdev.gnews.domain.model.news.Source(name = entity.sourceName)
                    )
                }
                emit(Resource.Success(TopHeadlineResponse(articles = articles, status = "ok", totalResults = articles.size)))
            } else {
                emit(Resource.Error("No internet connection and no cached data available."))
            }
        }
    }

    override suspend fun getEverything(
        query: String?,
        sources: String?,
        sortBy: String?,
        language: String?,
        page: Int,
        pageSize: Int
    ): Flow<Resource<EverythingResponse>>  = flow {
        emit(Resource.Loading())
        if(networkMonitor.isCurrentlyOnline()){
            val apiResponse = saveApiCall {
                apiService.getEverything(
                    query = query,
                    sources = sources,
                    sortBy = sortBy,
                    language = language,
                    page = page,
                    pageSize = pageSize
                )
            }
            emitAll(apiResponse)
        } else {
            emit(Resource.Error("No connection internet !"))
        }
    }

    override suspend fun getSources(
        category: String?,
        language: String?,
        country: String?
    ): Flow<Resource<SourcesResponse>> = flow {
        emit(Resource.Loading())
        if (networkMonitor.isCurrentlyOnline()) {
            val apiResponse = saveApiCall {
                apiService.getSources(
                    category = category,
                    language = language,
                    country = country
                )
            }
            emitAll(apiResponse)
        } else {
            emit(Resource.Error("No connection internet !"))
        }
    }

    override fun getAllFavoriteNews(): Flow<List<NewsEntity>> = newsDao.getAllFavoriteNews()
    override suspend fun insertFavoriteNews(news: NewsEntity) = newsDao.insertFavoriteNews(news)
    override suspend fun deleteFavoriteNews(news: NewsEntity) = newsDao.deleteFavoriteNews(news)
    override fun isFavoriteNews(url: String): Flow<Boolean> = newsDao.isFavoriteNews(url)

}
