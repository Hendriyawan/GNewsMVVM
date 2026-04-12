package com.hdev.gnews.presenter.trends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.ArticlesItem
import com.hdev.gnews.domain.model.news.EverythingResponse
import com.hdev.gnews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EverythingViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    private val _everythingState = MutableStateFlow<Resource<EverythingResponse>?>(null)
    val everythingState = _everythingState.asStateFlow()

    private var currentPage = 1
    private var currentQuery: String? = "news"
    private var currentCategory: String? = null
    private var isLastPage = false
    private val articlesList = mutableListOf<ArticlesItem>()

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
            repository.getEverything(
                query = searchQuery,
                page = currentPage,
                pageSize = 20
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val newArticles = resource.data?.articles?.filterNotNull() ?: emptyList()
                        if (newArticles.isEmpty()) {
                            isLastPage = true
                        } else {
                            articlesList.addAll(newArticles)
                        }
                        _everythingState.value = Resource.Success(
                            EverythingResponse(
                                articles = articlesList.toList(),
                                status = resource.data?.status,
                                totalResults = resource.data?.totalResults
                            )
                        )
                    }
                    is Resource.Error -> {
                        _everythingState.value = resource
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
