package com.hdev.gnews.presenter.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.Article
import com.hdev.gnews.domain.model.news.NewsResult
import com.hdev.gnews.domain.usecase.news.GetTopHeadlineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopHeadlineUseCase: GetTopHeadlineUseCase
) : ViewModel() {

    private val _topHeadlineState = MutableStateFlow<Resource<NewsResult>?>(null)
    val topHeadlineState = _topHeadlineState.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private var getTopHeadlineJob: kotlinx.coroutines.Job? = null
    
    private var currentPage = 1
    private var currentCategory: String? = null
    private var isLastPage = false
    private val articlesList = mutableListOf<Article>()

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
            getTopHeadlineUseCase(
                country = country,
                category = currentCategory,
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
                        _topHeadlineState.value = Resource.Success(
                            NewsResult(
                                articles = articlesList.toList(),
                                status = resource.data?.status,
                                totalResults = resource.data?.totalResults ?: articlesList.size
                            )
                        )
                    }
                    is Resource.Error -> {
                        _topHeadlineState.value = Resource.Error(resource.message ?: "failed to get data")
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