package com.hdev.gnews.presenter.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.SourcesResponse
import com.hdev.gnews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourcesViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    private val _sourcesState = MutableStateFlow<Resource<SourcesResponse>>(Resource.Loading())
    val sourcesState = _sourcesState.asStateFlow()

    fun getSources(category: String? = null) {
        viewModelScope.launch {
            repository.getSources(category = category).collect {
                _sourcesState.value = it
            }
        }
    }
}
