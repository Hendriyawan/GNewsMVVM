package com.hdev.gnews.presenter.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.data.source.local.room.entity.NewsEntity
import com.hdev.gnews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailNewsViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    fun isFavorite(url: String): Flow<Boolean> = repository.isFavoriteNews(url)

    fun toggleFavorite(news: NewsEntity, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                repository.deleteFavoriteNews(news)
            } else {
                repository.insertFavoriteNews(news)
            }
        }
    }
}