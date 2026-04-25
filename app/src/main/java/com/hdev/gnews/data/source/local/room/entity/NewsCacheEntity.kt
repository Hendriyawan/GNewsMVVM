package com.hdev.gnews.data.source.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val category: String
)
