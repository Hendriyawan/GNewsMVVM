package com.hdev.gnews.presenter.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.NewsResult
import com.hdev.gnews.domain.usecase.news.GetEverythingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceDetailViewModel @Inject constructor(
    private val getEverythingUseCase: GetEverythingUseCase
) : ViewModel() {

    private val _articlesState = MutableStateFlow<Resource<NewsResult>>(Resource.Loading())
    val articlesState = _articlesState.asStateFlow()

    fun getArticlesBySource(sourceId: String) {
        viewModelScope.launch {
            getEverythingUseCase(sources = sourceId).collect {
                _articlesState.value = it
            }
        }
    }
}
