package com.hdev.gnews.presenter.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.EverythingResponse
import com.hdev.gnews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceDetailViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    private val _articlesState = MutableStateFlow<Resource<EverythingResponse>>(Resource.Loading())
    val articlesState = _articlesState.asStateFlow()

    fun getArticlesBySource(sourceId: String) {
        viewModelScope.launch {
            repository.getEverything(sources = sourceId).collect {
                _articlesState.value = it
            }
        }
    }
}
