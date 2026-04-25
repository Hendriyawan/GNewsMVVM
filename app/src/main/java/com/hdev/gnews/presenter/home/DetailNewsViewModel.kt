package com.hdev.gnews.presenter.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.gnews.domain.model.news.Article
import com.hdev.gnews.domain.usecase.news.DeleteFavoriteNewsUseCase
import com.hdev.gnews.domain.usecase.news.IsFavoriteNewsUseCase
import com.hdev.gnews.domain.usecase.news.SaveFavoriteNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailNewsViewModel @Inject constructor(
    private val isFavoriteNewsUseCase: IsFavoriteNewsUseCase,
    private val saveFavoriteNewsUseCase: SaveFavoriteNewsUseCase,
    private val deleteFavoriteNewsUseCase: DeleteFavoriteNewsUseCase
) : ViewModel() {

    fun isFavorite(url: String): Flow<Boolean> = isFavoriteNewsUseCase(url)

    fun toggleFavorite(article: Article, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                deleteFavoriteNewsUseCase(article)
            } else {
                saveFavoriteNewsUseCase(article)
            }
        }
    }
}