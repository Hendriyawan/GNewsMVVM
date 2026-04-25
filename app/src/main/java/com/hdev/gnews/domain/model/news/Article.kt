package com.hdev.gnews.domain.model.news

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleSource(
    val id: String? = null,
    val name: String? = null
) : Parcelable

@Parcelize
data class Article(
    val url: String = "",
    val title: String? = null,
    val author: String? = null,
    val description: String? = null,
    val urlToImage: String? = null,
    val publishedAt: String? = null,
    val content: String? = null,
    val source: ArticleSource? = null
) : Parcelable

