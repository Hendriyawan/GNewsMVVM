package com.hdev.gnews.domain.model.news

data class NewsResult(
    val status: String? = null,
    val totalResults: Int = 0,
    val articles: List<Article> = emptyList()
)
