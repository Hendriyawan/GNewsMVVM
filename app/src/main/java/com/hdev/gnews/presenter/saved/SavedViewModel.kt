package com.hdev.gnews.presenter.saved

import androidx.lifecycle.ViewModel
import com.hdev.gnews.domain.model.news.Article
import com.hdev.gnews.domain.usecase.news.GetFavoriteNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val getFavoriteNewsUseCase: GetFavoriteNewsUseCase
) : ViewModel() {
    val favoriteNews: Flow<List<Article>> = getFavoriteNewsUseCase()
}