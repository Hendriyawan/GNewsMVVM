package com.hdev.gnews.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class NewsResponseDto(
    @field:SerializedName("totalResults")
    val totalResults: Int? = null,
    @field:SerializedName("articles")
    val articles: List<ArticleDto?>? = null,
    @field:SerializedName("status")
    val status: String? = null
)

data class ArticleSourceDto(
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("id")
    val id: String? = null
)

data class ArticleDto(
    @field:SerializedName("publishedAt")
    val publishedAt: String? = null,
    @field:SerializedName("author")
    val author: String? = null,
    @field:SerializedName("urlToImage")
    val urlToImage: String? = null,
    @field:SerializedName("description")
    val description: String? = null,
    @field:SerializedName("source")
    val source: ArticleSourceDto? = null,
    @field:SerializedName("title")
    val title: String? = null,
    @field:SerializedName("url")
    val url: String? = null,
    @field:SerializedName("content")
    val content: String? = null
)
