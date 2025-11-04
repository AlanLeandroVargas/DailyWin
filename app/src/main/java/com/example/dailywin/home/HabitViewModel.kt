package com.example.dailywin.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywin.data.model.Frequency
import com.example.dailywin.data.model.Habit
import com.example.dailywin.data.repository.HabitRepository
import com.example.dailywin.notification.NotificationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

class HabitViewModel(private val repository: HabitRepository, private val context: Context) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val initialHabits = repository.getHabits()
            _habits.value = initialHabits.map { it.copy(streak = calculateStreak(it)) }
        }
    }

    fun isHabitDueOnDate(habit: Habit, date: LocalDate): Boolean {
        if (date.isBefore(habit.startDate) || (habit.endDate != null && date.isAfter(habit.endDate))) {
            return false
        }

        return when (habit.frequency) {
            Frequency.DAILY -> true
            Frequency.WEEKLY -> {
                val dayOfWeek = when (date.dayOfWeek) {
                    DayOfWeek.MONDAY -> "L"
                    DayOfWeek.TUESDAY -> "M"
                    DayOfWeek.WEDNESDAY -> "X"
                    DayOfWeek.THURSDAY -> "J"
                    DayOfWeek.FRIDAY -> "V"
                    DayOfWeek.SATURDAY -> "S"
                    DayOfWeek.SUNDAY -> "D"
                }
                habit.daysOfWeek.contains(dayOfWeek)
            }
            Frequency.MONTHLY -> date.dayOfMonth == habit.startDate.dayOfMonth
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            val newHabit = habit.copy(id = UUID.randomUUID().toString())
            repository.addHabit(newHabit)
            if (newHabit.time.isNotBlank()) {
                NotificationUtils.scheduleNotification(context, newHabit)
            }
            loadInitialData()
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
            if (habit.time.isNotBlank()) {
                NotificationUtils.scheduleNotification(context, habit)
            } else {
                NotificationUtils.cancelNotification(context, habit)
            }
            loadInitialData()
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            val habit = _habits.value.find { it.id == id }
            repository.deleteHabit(id)
            habit?.let { NotificationUtils.cancelNotification(context, it) }
            loadInitialData()
        }
    }

    fun toggleCompleted(id: String, date: LocalDate) {
        viewModelScope.launch {
            val habit = _habits.value.find { it.id == id }
            if (habit != null) {
                val newCompletedDates = if (habit.completedDates.contains(date)) {
                    habit.completedDates - date
                } else {
                    habit.completedDates + date
                }
                val updatedHabit = habit.copy(completedDates = newCompletedDates)
                repository.updateHabit(updatedHabit.copy(streak = calculateStreak(updatedHabit)))
                loadInitialData()
            }
        }
    }

    private fun calculateStreak(habit: Habit): Int {
        val completedDates = habit.completedDates.sortedDescending()
        if (completedDates.isEmpty()) {
            return 0
        }

        var streak = 0
        var currentDate = LocalDate.now()

        if (!completedDates.contains(currentDate) && isHabitDueOnDate(habit, currentDate)) {
            return 0
        }

        for (date in completedDates) {
            if (isHabitDueOnDate(habit, currentDate)) {
                if (date == currentDate) {
                    streak++
                    currentDate = currentDate.minusDays(1)
                } else if (date.isBefore(currentDate)) {
                    break
                }
            } else {
                currentDate = currentDate.minusDays(1)
            }
        }
        return streak
    }

    fun getWeeklyCompletionData(habit: Habit): List<Float> {
        val today = LocalDate.now()
        val weekStart = today.with(DayOfWeek.MONDAY)
        return (0..6).map { i ->
            val date = weekStart.plusDays(i.toLong())
            if (habit.completedDates.contains(date)) 1f else 0f
        }
    }

    fun getHabitById(id: String): Habit? {
        return _habits.value.find { it.id == id }
    }

    fun signOut() {
        repository.signOut()
    }
}
