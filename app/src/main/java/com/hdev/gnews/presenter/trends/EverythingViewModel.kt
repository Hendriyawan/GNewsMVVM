package com.hdev.gnews.presenter.trends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.Article
import com.hdev.gnews.domain.model.news.NewsResult
import com.hdev.gnews.domain.usecase.news.GetEverythingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EverythingViewModel @Inject constructor(
    private val getEverythingUseCase: GetEverythingUseCase
) : ViewModel() {

    private val _everythingState = MutableStateFlow<Resource<NewsResult>?>(null)
    val everythingState = _everythingState.asStateFlow()

    private var currentPage = 1
    private var currentQuery: String? = "news"
    private var currentCategory: String? = null
    private var isLastPage = false
    private val articlesList = mutableListOf<Article>()

    fun getEverything(
        query: String? = "news",
        category: String? = null,
        isLoadMore: Boolean = false
    ) {
        if (isLoadMore) {
            if (isLastPage) return
            currentPage++
        } else {
            currentPage = 1
            isLastPage = false
            articlesList.clear()
            currentQuery = if (query.isNullOrEmpty()) "news" else query
            currentCategory = category
        }

        val searchQuery = if (currentCategory != null) {
            "$currentQuery AND $currentCategory"
        } else {
            currentQuery
        }

        viewModelScope.launch {
            getEverythingUseCase(
                query = searchQuery,
                page = currentPage,
                pageSize = 20
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val newArticles = resource.data?.articles ?: emptyList()
                        if (newArticles.isEmpty()) {
                            isLastPage = true
                        } else {
                            articlesList.addAll(newArticles)
                        }
                        _everythingState.value = Resource.Success(
                            NewsResult(
                                articles = articlesList.toList(),
                                status = resource.data?.status,
                                totalResults = resource.data?.totalResults ?: articlesList.size
                            )
                        )
                    }
                    is Resource.Error -> {
                        _everythingState.value = Resource.Error(resource.message ?: "failed to get data")
                    }
                    is Resource.Loading -> {
                        if (!isLoadMore) {
                            _everythingState.value = Resource.Loading()
                        }
                    }
                }
            }
        }
    }
}

