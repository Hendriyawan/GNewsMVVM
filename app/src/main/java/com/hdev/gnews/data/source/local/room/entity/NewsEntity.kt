package com.hdev.gnews.data.source.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_favorite")
data class NewsEntity(
    @PrimaryKey
    val url: String,
    val title: String?,
    val author: String?,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
    val sourceName: String?
)
