package com.example.dailywin.data.model

data class Habit(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val time: String = "",
    val reminders: List<String> = emptyList(),
    val priority: Priority = Priority.MEDIUM,
    val frequency: Frequency = Frequency.DAILY,
    val startDate: String = "",
    val endDate: String = "",
    val dailyGoal: String = "",
    val additionalGoal: String = "",
    val completed: Boolean = false,
    val streak: Int = 0,
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

enum class Frequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}