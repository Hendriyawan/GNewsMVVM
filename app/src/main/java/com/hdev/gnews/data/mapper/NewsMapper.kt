package com.hdev.gnews.data.mapper

import com.hdev.gnews.data.source.local.room.entity.NewsCacheEntity
import com.hdev.gnews.data.source.local.room.entity.NewsEntity
import com.hdev.gnews.data.source.remote.dto.ArticleDto
import com.hdev.gnews.data.source.remote.dto.ArticleSourceDto
import com.hdev.gnews.data.source.remote.dto.NewsResponseDto
import com.hdev.gnews.data.source.remote.dto.NewsSourceDto
import com.hdev.gnews.data.source.remote.dto.SourcesResponseDto
import com.hdev.gnews.domain.model.news.Article
import com.hdev.gnews.domain.model.news.ArticleSource
import com.hdev.gnews.domain.model.news.NewsResult
import com.hdev.gnews.domain.model.news.NewsSource
import com.hdev.gnews.domain.model.news.SourcesResult

fun NewsResponseDto.toDomain(): NewsResult = NewsResult(
    status = status,
    totalResults = totalResults ?: 0,
    articles = articles.orEmpty().mapNotNull { it?.toDomain() }
)

fun ArticleDto.toDomain(): Article = Article(
    url = url.orEmpty(),
    title = title,
    author = author,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
    source = source?.toDomain()
)

fun ArticleSourceDto.toDomain(): ArticleSource = ArticleSource(
    id = id,
    name = name
)

fun SourcesResponseDto.toDomain(): SourcesResult = SourcesResult(
    status = status,
    sources = sources.orEmpty().mapNotNull { it?.toDomain() }
)

fun NewsSourceDto.toDomain(): NewsSource = NewsSource(
    id = id.orEmpty(),
    name = name,
    description = description,
    category = category,
    language = language,
    country = country,
    url = url
)

fun Article.toEntity(): NewsEntity = NewsEntity(
    url = url,
    title = title,
    author = author,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
    sourceName = source?.name
)

fun NewsEntity.toDomain(): Article = Article(
    url = url,
    title = title,
    author = author,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
    source = ArticleSource(name = sourceName)
)

fun Article.toCacheEntity(category: String): NewsCacheEntity = NewsCacheEntity(
    url = url,
    title = title,
    author = author,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
    sourceName = source?.name,
    category = category
)

fun NewsCacheEntity.toDomain(): Article = Article(
    url = url,
    title = title,
    author = author,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
    source = ArticleSource(name = sourceName)
)
