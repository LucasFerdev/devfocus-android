package br.com.devfocus.domain.model

import br.com.devfocus.data.local.entity.StudyStatus
import java.time.LocalDate

data class StudyDay(
    val date: LocalDate,
    val status: StudyStatus?,
    val isToday: Boolean = false,
    val isFuture: Boolean = false
)
