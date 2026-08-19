package br.com.devfocus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.devfocus.data.local.entity.StudyDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_history ORDER BY date DESC")
    fun getAllStudyHistory(): Flow<List<StudyDayEntity>>

    @Query("SELECT * FROM study_history WHERE date = :date")
    suspend fun getStudyDayByDate(date: String): StudyDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyDay(studyDay: StudyDayEntity)

    @Query("SELECT * FROM study_history WHERE date >= :startDate ORDER BY date ASC")
    fun getStudyHistoryFrom(startDate: String): Flow<List<StudyDayEntity>>
}
