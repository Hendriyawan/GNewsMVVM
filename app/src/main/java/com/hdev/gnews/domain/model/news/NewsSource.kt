package com.hdev.gnews.domain.model.news

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsSource(
    val id: String = "",
    val name: String? = null,
    val description: String? = null,
    val category: String? = null,
    val language: String? = null,
    val country: String? = null,
    val url: String? = null
) : Parcelable

data class SourcesResult(
    val status: String? = null,
    val sources: List<NewsSource> = emptyList()
)

typealias SourcesItem = NewsSource
