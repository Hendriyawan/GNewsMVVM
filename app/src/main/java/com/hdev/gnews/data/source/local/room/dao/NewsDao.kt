package com.hdev.gnews.data.source.local.room.dao

import androidx.room.*
import com.hdev.gnews.data.source.local.room.entity.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_favorite")
    fun getAllFavoriteNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteNews(news: NewsEntity)

    @Delete
    suspend fun deleteFavoriteNews(news: NewsEntity)

    @Query("SELECT EXISTS(SELECT * FROM news_favorite WHERE url = :url)")
    fun isFavoriteNews(url: String): Flow<Boolean>

    // New methods for Offline Mode (Cache)
    @Query("SELECT * FROM news_cache WHERE category = :category")
    suspend fun getNewsCache(category: String): List<NewsCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsCache(news: List<NewsCacheEntity>)

    @Query("DELETE FROM news_cache WHERE category = :category")
    suspend fun deleteNewsCache(category: String)
}

@Entity(tableName = "news_cache")
data class NewsCacheEntity(
    @PrimaryKey
    val url: String,
    val title: String?,
    val author: String?,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
    val sourceName: String?,
    val category: String // To filter cache by category
)
