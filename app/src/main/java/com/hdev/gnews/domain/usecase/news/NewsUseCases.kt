package com.hdev.gnews.domain.usecase.news

import com.hdev.gnews.domain.model.news.Article
import com.hdev.gnews.domain.repository.NewsRepository
import javax.inject.Inject

class GetTopHeadlineUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(
        country: String = "us",
        category: String? = null,
        page: Int = 1,
        pageSize: Int = 20
    ) = repository.getTopHeadline(
        country = country,
        category = category,
        page = page,
        pageSize = pageSize
    )
}

class GetEverythingUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(
        query: String? = null,
        sources: String? = null,
        sortBy: String? = "publishedAt",
        language: String? = "en",
        page: Int = 1,
        pageSize: Int = 20
    ) = repository.getEverything(
        query = query,
        sources = sources,
        sortBy = sortBy,
        language = language,
        page = page,
        pageSize = pageSize
    )
}

class GetSourcesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(
        category: String? = null,
        language: String? = "en",
        country: String? = "us"
    ) = repository.getSources(
        category = category,
        language = language,
        country = country
    )
}

class GetFavoriteNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke() = repository.getFavoriteNews()
}

class SaveFavoriteNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(article: Article) = repository.saveFavoriteNews(article)
}

class DeleteFavoriteNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(article: Article) = repository.deleteFavoriteNews(article)
}

class IsFavoriteNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(url: String) = repository.isFavoriteNews(url)
}
