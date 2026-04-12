package com.hdev.gnews.presenter.saved

import androidx.lifecycle.ViewModel
import com.hdev.gnews.data.source.local.room.entity.NewsEntity
import com.hdev.gnews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {
    val favoriteNews: Flow<List<NewsEntity>> = repository.getAllFavoriteNews()
}