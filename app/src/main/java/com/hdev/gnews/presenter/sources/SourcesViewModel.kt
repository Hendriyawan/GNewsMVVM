package com.hdev.gnews.presenter.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.SourcesResult
import com.hdev.gnews.domain.usecase.news.GetSourcesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourcesViewModel @Inject constructor(
    private val getSourcesUseCase: GetSourcesUseCase
) : ViewModel() {

    private val _sourcesState = MutableStateFlow<Resource<SourcesResult>>(Resource.Loading())
    val sourcesState = _sourcesState.asStateFlow()

    fun getSources(category: String? = null) {
        viewModelScope.launch {
            getSourcesUseCase(category = category).collect {
                _sourcesState.value = it
            }
        }
    }
}
