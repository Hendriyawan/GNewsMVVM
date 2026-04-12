package com.hdev.gnews.presenter.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.ArticlesItem
import com.hdev.gnews.domain.model.news.EverythingResponse
import com.hdev.gnews.domain.model.news.TopHeadlineResponse
import com.hdev.gnews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    private val _topHeadlineState = MutableStateFlow<Resource<TopHeadlineResponse>?>(null)
    val topHeadlineState = _topHeadlineState.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private var getTopHeadlineJob: kotlinx.coroutines.Job? = null
    
    private var currentPage = 1
    private var currentCategory: String? = null
    private var isLastPage = false
    private val articlesList = mutableListOf<ArticlesItem>()

    fun getTopHeadline(
        country: String = "us",
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
            currentCategory = category
            getTopHeadlineJob?.cancel()
        }

        getTopHeadlineJob = viewModelScope.launch {
            repository.getTopHeadline(
                country = country,
                category = currentCategory,
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
                        _topHeadlineState.value = Resource.Success(
                            TopHeadlineResponse(
                                articles = articlesList.toList(),
                                status = resource.data?.status,
                                totalResults = resource.data?.totalResults
                            )
                        )
                    }
                    is Resource.Error -> {
                        _topHeadlineState.value = resource
                        _errorMessage.emit(resource.message ?: "failed to get data")
                    }
                    is Resource.Loading -> {
                        if (!isLoadMore) {
                            _topHeadlineState.value = Resource.Loading()
                        }
                    }
                }
            }
        }
    }
}