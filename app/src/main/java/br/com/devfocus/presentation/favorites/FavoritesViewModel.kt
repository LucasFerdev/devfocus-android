package br.com.devfocus.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.devfocus.data.local.entity.QuoteEntity
import br.com.devfocus.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    val favorites: StateFlow<List<QuoteEntity>> = quoteRepository.getFavoriteQuotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            quoteRepository.toggleFavorite(id, !isFavorite)
        }
    }
}
