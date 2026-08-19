package br.com.devfocus.presentation.home

import br.com.devfocus.data.local.entity.QuoteEntity
import br.com.devfocus.domain.model.StudyDay

data class HomeUiState(
    val quote: QuoteEntity? = null,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val studiedToday: Boolean = false,
    val weekDays: List<StudyDay> = emptyList(),
    val favoriteQuotes: List<QuoteEntity> = emptyList(),
    val freezeAvailable: Boolean = true,
    val missedYesterday: Boolean = false,
    val isLoading: Boolean = true
)
