package br.com.devfocus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_history")
data class StudyDayEntity(
    @PrimaryKey
    val date: String, // ISO format: yyyy-MM-dd
    val status: StudyStatus
)

enum class StudyStatus {
    STUDIED,
    FROZEN
}
