package br.com.devfocus.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "devfocus_prefs")

class DevFocusPreferences(private val context: Context) {

    companion object {
        private val DAILY_QUOTE_ID = longPreferencesKey("daily_quote_id")
        private val DAILY_QUOTE_DATE = stringPreferencesKey("daily_quote_date")
        private val BEST_STREAK = longPreferencesKey("best_streak")
    }

    val dailyQuoteId: Flow<Long?> = context.dataStore.data.map { it[DAILY_QUOTE_ID] }
    val dailyQuoteDate: Flow<String?> = context.dataStore.data.map { it[DAILY_QUOTE_DATE] }
    val bestStreak: Flow<Int> = context.dataStore.data.map { it[BEST_STREAK]?.toInt() ?: 0 }

    suspend fun saveDailyQuote(id: Long, date: String) {
        context.dataStore.edit {
            it[DAILY_QUOTE_ID] = id
            it[DAILY_QUOTE_DATE] = date
        }
    }

    suspend fun saveBestStreak(streak: Int) {
        context.dataStore.edit {
            it[BEST_STREAK] = streak.toLong()
        }
    }
}
