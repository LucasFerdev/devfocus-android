package br.com.devfocus.domain.repository

import br.com.devfocus.data.local.entity.QuoteEntity
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    fun getDailyQuote(): Flow<QuoteEntity?>
    fun getFavoriteQuotes(): Flow<List<QuoteEntity>>
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    suspend fun seedQuotesIfEmpty()
    suspend fun refreshDailyQuote()
}
