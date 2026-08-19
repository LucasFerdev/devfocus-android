package br.com.devfocus.data.repository

import br.com.devfocus.data.local.dao.StudyDao
import br.com.devfocus.data.local.entity.StudyDayEntity
import br.com.devfocus.data.local.entity.StudyStatus
import br.com.devfocus.data.local.preferences.DevFocusPreferences
import br.com.devfocus.domain.logic.StreakCalculator
import br.com.devfocus.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class StudyRepositoryImpl(
    private val studyDao: StudyDao,
    private val preferences: DevFocusPreferences
) : StudyRepository {

    override fun getStudyHistory(): Flow<List<StudyDayEntity>> = studyDao.getAllStudyHistory()

    override fun getStudyHistoryFrom(startDate: LocalDate): Flow<List<StudyDayEntity>> {
        return studyDao.getStudyHistoryFrom(startDate.toString())
    }

    override suspend fun registerStudy(date: LocalDate, status: StudyStatus) {
        val existing = studyDao.getStudyDayByDate(date.toString())
        if (existing == null) {
            studyDao.insertStudyDay(StudyDayEntity(date.toString(), status))
            
            // Recalculate best streak after registering study
            val history = studyDao.getAllStudyHistory().first()
            val currentStreak = StreakCalculator.calculateCurrentStreak(history, LocalDate.now())
            val best = preferences.bestStreak.first()
            if (currentStreak > best) {
                preferences.saveBestStreak(currentStreak)
            }
        }
    }

    override suspend fun getStudyDay(date: LocalDate): StudyDayEntity? {
        return studyDao.getStudyDayByDate(date.toString())
    }

    override fun getCurrentStreak(): Flow<Int> {
        return studyDao.getAllStudyHistory().map { history ->
            StreakCalculator.calculateCurrentStreak(history, LocalDate.now())
        }
    }

    override fun getBestStreak(): Flow<Int> = preferences.bestStreak

    override fun getStudiedDaysCount(): Flow<Int> {
        return studyDao.getAllStudyHistory().map { history ->
            StreakCalculator.calculateTotalStudiedDays(history)
        }
    }
}
