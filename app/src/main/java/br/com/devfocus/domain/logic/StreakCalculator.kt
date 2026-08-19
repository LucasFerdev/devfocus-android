package br.com.devfocus.domain.logic

import br.com.devfocus.data.local.entity.StudyDayEntity
import br.com.devfocus.data.local.entity.StudyStatus
import java.time.LocalDate

object StreakCalculator {

    fun calculateCurrentStreak(history: List<StudyDayEntity>, today: LocalDate): Int {
        if (history.isEmpty()) return 0

        val historyMap = history.associate { LocalDate.parse(it.date) to it.status }
        var streak = 0
        var currentDate = today

        // If today is not in history, check if yesterday was studied or frozen to continue streak
        if (!historyMap.containsKey(today)) {
            currentDate = today.minusDays(1)
        }

        while (true) {
            val status = historyMap[currentDate]
            if (status != null) {
                // Both STUDIED and FROZEN maintain the streak
                // But only STUDIED is counted for the "visual" streak number in some contexts?
                // The requirement says: "FROZEN mantém sequência, mas não representa estudo real."
                // "STUDIED mantém e incrementa sequência."
                // Usually "Streak" means consecutive days of active participation (or protected days)
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }

    fun calculateTotalStudiedDays(history: List<StudyDayEntity>): Int {
        return history.count { it.status == StudyStatus.STUDIED }
    }
    
    fun isFreezeAvailableForWeek(history: List<StudyDayEntity>, dateInWeek: LocalDate): Boolean {
        // Week is Monday to Sunday
        val monday = dateInWeek.with(java.time.DayOfWeek.MONDAY)
        val sunday = dateInWeek.with(java.time.DayOfWeek.SUNDAY)
        
        val weekHistory = history.filter { 
            val d = LocalDate.parse(it.date)
            !d.isBefore(monday) && !d.isAfter(sunday)
        }
        
        return weekHistory.none { it.status == StudyStatus.FROZEN }
    }
}
