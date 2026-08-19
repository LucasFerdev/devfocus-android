package br.com.devfocus.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.devfocus.data.local.entity.StudyDayEntity
import br.com.devfocus.data.local.entity.StudyStatus
import br.com.devfocus.domain.logic.StreakCalculator
import br.com.devfocus.domain.model.StudyDay
import br.com.devfocus.domain.repository.QuoteRepository
import br.com.devfocus.domain.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HomeViewModel(
    private val quoteRepository: QuoteRepository,
    private val studyRepository: StudyRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        quoteRepository.getDailyQuote(),
        studyRepository.getCurrentStreak(),
        studyRepository.getBestStreak(),
        studyRepository.getStudyHistory(),
        quoteRepository.getFavoriteQuotes()
    ) { quote, currentStreak, bestStreak, history, favorites ->
        val today = LocalDate.now()
        val studiedToday = history.any { it.date == today.toString() && it.status == StudyStatus.STUDIED }
        
        val weekDays = calculateWeekDays(history, today)
        val freezeAvailable = StreakCalculator.isFreezeAvailableForWeek(history, today)
        
        val yesterday = today.minusDays(1)
        val studiedYesterday = history.any { it.date == yesterday.toString() }
        val missedYesterday = !studiedYesterday && !studiedToday // Simplified missed yesterday check
        
        HomeUiState(
            quote = quote,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            studiedToday = studiedToday,
            weekDays = weekDays,
            favoriteQuotes = favorites.take(3), // Preview of 3 favorites
            freezeAvailable = freezeAvailable,
            missedYesterday = missedYesterday,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            quoteRepository.seedQuotesIfEmpty()
        }
    }

    fun onStudyTodayClicked() {
        viewModelScope.launch {
            studyRepository.registerStudy(LocalDate.now(), StudyStatus.STUDIED)
        }
    }

    fun onFavoriteClicked(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            quoteRepository.toggleFavorite(id, !isFavorite)
        }
    }
    
    fun onUseFreezeClicked() {
        viewModelScope.launch {
            val yesterday = LocalDate.now().minusDays(1)
            studyRepository.registerStudy(yesterday, StudyStatus.FROZEN)
        }
    }

    private fun calculateWeekDays(history: List<StudyDayEntity>, today: LocalDate): List<StudyDay> {
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val historyMap = history.associate { it.date to it.status }
        
        return (0..6).map { i ->
            val date = monday.plusDays(i.toLong())
            StudyDay(
                date = date,
                status = historyMap[date.toString()],
                isToday = date == today,
                isFuture = date.isAfter(today)
            )
        }
    }
}
