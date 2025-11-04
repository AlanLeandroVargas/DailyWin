package com.example.dailywin.data.model

import java.time.LocalDate

data class HabitDTO (
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val time: String = "",
    val reminders: List<String> = emptyList(),
    val priority: Priority = Priority.LOW,
    val frequency: Frequency = Frequency.DAILY,
    val startDate: String = "",
    val endDate: String = "",
    val dailyGoal: String = "",
    val additionalGoal: String = "",
    val completed: Boolean = false,
    val streak: Int = 0,
    val daysOfWeek: List<String> = emptyList(),
    val completedDates: List<String> = emptyList()
) {
    fun toHabit(): Habit {
        return Habit(
            id = this.id,
            userId = this.userId,
            name = this.name,
            category = this.category,
            description = this.description,
            time = this.time,
            reminders = this.reminders,
            priority = this.priority,
            frequency = this.frequency,
            startDate = LocalDate.parse(this.startDate),
            endDate = LocalDate.parse(this.endDate),
            dailyGoal = this.dailyGoal,
            additionalGoal = this.additionalGoal,
            completed = this.completed,
            streak = this.streak,
            daysOfWeek = this.daysOfWeek
        )
    }
}