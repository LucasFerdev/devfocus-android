package br.com.devfocus.domain.repository

import br.com.devfocus.data.local.entity.StudyDayEntity
import br.com.devfocus.data.local.entity.StudyStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface StudyRepository {
    fun getStudyHistory(): Flow<List<StudyDayEntity>>
    fun getStudyHistoryFrom(startDate: LocalDate): Flow<List<StudyDayEntity>>
    suspend fun registerStudy(date: LocalDate, status: StudyStatus)
    suspend fun getStudyDay(date: LocalDate): StudyDayEntity?
    fun getCurrentStreak(): Flow<Int>
    fun getBestStreak(): Flow<Int>
    fun getStudiedDaysCount(): Flow<Int>
}
