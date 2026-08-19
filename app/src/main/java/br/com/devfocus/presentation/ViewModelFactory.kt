package br.com.devfocus.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.devfocus.data.local.database.DevFocusDatabase
import br.com.devfocus.data.local.preferences.DevFocusPreferences
import br.com.devfocus.data.repository.QuoteRepositoryImpl
import br.com.devfocus.data.repository.StudyRepositoryImpl
import br.com.devfocus.presentation.home.HomeViewModel

class ViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val database = DevFocusDatabase.getDatabase(context)
    private val preferences = DevFocusPreferences(context)
    
    private val quoteRepository = QuoteRepositoryImpl(database.quoteDao(), preferences)
    private val studyRepository = StudyRepositoryImpl(database.studyDao(), preferences)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(quoteRepository, studyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
