package com.example.dailywin.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.String

data class Habit(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val time: String = "",
    val reminders: List<String> = emptyList(),
    val priority: Priority = Priority.LOW,
    val frequency: Frequency = Frequency.DAILY,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val dailyGoal: String = "",
    val additionalGoal: String = "",
    val completed: Boolean = false,
    val streak: Int = 0,
    val daysOfWeek: List<String> = emptyList(),
    val completedDates: List<LocalDate> = emptyList()
){
    fun toHabitDTO(): HabitDTO {
        return HabitDTO(
            id = this.id,
            userId = this.userId,
            name = this.name,
            category = this.category,
            description = this.description,
            time = this.time,
            reminders = this.reminders,
            priority = this.priority,
            frequency = this.frequency,
            startDate = this.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            endDate = this.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            dailyGoal = this.dailyGoal,
            additionalGoal = this.additionalGoal,
            completed = this.completed,
            streak = this.streak,
            daysOfWeek = this.daysOfWeek,
            completedDates = this.completedDates.map { it.toString() }
        )
    }
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

enum class Frequency {
    DAILY,
    WEEKLY,
    MONTHLY,
}