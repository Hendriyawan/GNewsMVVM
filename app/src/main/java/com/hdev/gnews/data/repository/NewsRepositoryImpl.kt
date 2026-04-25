package com.hdev.gnews.data.repository

import com.hdev.gnews.data.mapper.toCacheEntity
import com.hdev.gnews.data.mapper.toDomain
import com.hdev.gnews.data.mapper.toEntity
import com.hdev.gnews.core.utils.NetworkMonitor
import com.hdev.gnews.data.source.local.room.dao.NewsDao
import com.hdev.gnews.data.source.remote.ApiService
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.Article
import com.hdev.gnews.domain.model.news.NewsResult
import com.hdev.gnews.domain.model.news.SourcesResult
import com.hdev.gnews.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao,
    private val networkMonitor: NetworkMonitor
) : ResponseHelper(), NewsRepository {

    companion object {
        private const val DEFAULT_CATEGORY = "general"
        private const val OFFLINE_ERROR = "No internet connection and no cached data available."
    }

    override fun getTopHeadline(
        country: String,
        category: String?,
        page: Int,
        pageSize: Int
    ): Flow<Resource<NewsResult>> = flow {
        emit(Resource.Loading())

        val cacheCategory = category ?: DEFAULT_CATEGORY

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
                when (resource) {
                    is Resource.Success -> {
                        val newsResult = resource.data?.toDomain() ?: NewsResult()
                        val cacheList = newsResult.articles.map { article ->
                            article.toCacheEntity(cacheCategory)
                        }
                        newsDao.deleteNewsCache(cacheCategory)
                        newsDao.insertNewsCache(cacheList)
                        emit(Resource.Success(newsResult))
                    }
                    is Resource.Error -> emit(
                        Resource.Error(
                            resource.message ?: "Failed to fetch top headlines."
                        )
                    )
                    is Resource.Loading -> Unit
                }
            }
        } else {
            val localData = newsDao.getNewsCache(cacheCategory)
            if (localData.isNotEmpty()) {
                emit(
                    Resource.Success(
                        NewsResult(
                            status = "ok",
                            totalResults = localData.size,
                            articles = localData.map { entity -> entity.toDomain() }
                        )
                    )
                )
            } else {
                emit(Resource.Error(OFFLINE_ERROR))
            }
        }
    }

    override fun getEverything(
        query: String?,
        sources: String?,
        sortBy: String?,
        language: String?,
        page: Int,
        pageSize: Int
    ): Flow<Resource<NewsResult>>  = flow {
        emit(Resource.Loading())
        if(networkMonitor.isCurrentlyOnline()){
            emitAll(saveApiCall {
                apiService.getEverything(
                    query = query,
                    sources = sources,
                    sortBy = sortBy,
                    language = language,
                    page = page,
                    pageSize = pageSize
                )
            }.map { resource ->
                when (resource) {
                    is Resource.Success -> Resource.Success(resource.data?.toDomain() ?: NewsResult())
                    is Resource.Error -> Resource.Error(resource.message ?: "Failed to fetch articles.")
                    is Resource.Loading -> Resource.Loading()
                }
            })
        } else {
            emit(Resource.Error("No internet connection."))
        }
    }

    override fun getSources(
        category: String?,
        language: String?,
        country: String?
    ): Flow<Resource<SourcesResult>> = flow {
        emit(Resource.Loading())
        if (networkMonitor.isCurrentlyOnline()) {
            emitAll(saveApiCall {
                apiService.getSources(
                    category = category,
                    language = language,
                    country = country
                )
            }.map { resource ->
                when (resource) {
                    is Resource.Success -> Resource.Success(resource.data?.toDomain() ?: SourcesResult())
                    is Resource.Error -> Resource.Error(resource.message ?: "Failed to fetch sources.")
                    is Resource.Loading -> Resource.Loading()
                }
            })
        } else {
            emit(Resource.Error("No internet connection."))
        }
    }

    override fun getFavoriteNews(): Flow<List<Article>> =
        newsDao.getAllFavoriteNews().map { entities -> entities.map { entity -> entity.toDomain() } }

    override suspend fun saveFavoriteNews(article: Article) =
        newsDao.insertFavoriteNews(article.toEntity())

    override suspend fun deleteFavoriteNews(article: Article) =
        newsDao.deleteFavoriteNews(article.toEntity())

    override fun isFavoriteNews(url: String): Flow<Boolean> = newsDao.isFavoriteNews(url)

}
