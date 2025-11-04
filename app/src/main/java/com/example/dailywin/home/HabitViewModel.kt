package com.example.dailywin.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywin.data.model.Frequency
import com.example.dailywin.data.model.Habit
import com.example.dailywin.data.model.Priority
import com.example.dailywin.data.repository.HabitRepository
import com.example.dailywin.notification.NotificationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

class HabitViewModel(private val repository: HabitRepository, private val context: Context) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private var idCounter = 1L

    init {
        loadInitialData()
    }

    fun isHabitDueOnDate(habit: Habit, date: LocalDate): Boolean {
        if (date.isBefore(habit.startDate) || date.isAfter(habit.endDate)) return false

        return when (habit.frequency) {
            Frequency.DAILY -> true
            Frequency.WEEKLY -> {
                val dayOfWeek = when (date.dayOfWeek) {
                    java.time.DayOfWeek.MONDAY -> "L"
                    java.time.DayOfWeek.TUESDAY -> "M"
                    java.time.DayOfWeek.WEDNESDAY -> "X"
                    java.time.DayOfWeek.THURSDAY -> "J"
                    java.time.DayOfWeek.FRIDAY -> "V"
                    java.time.DayOfWeek.SATURDAY -> "S"
                    java.time.DayOfWeek.SUNDAY -> "D"
                }
                habit.daysOfWeek.contains(dayOfWeek)
            }
            Frequency.MONTHLY -> habit.startDate.dayOfMonth == date.dayOfMonth
        }
    }

    fun getHabitsForDate(date: LocalDate): List<Habit> {
        return _habits.value.filter { isHabitDueOnDate(it, date) }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val initialHabits = repository.getHabits()
            _habits.value = initialHabits
        }
    }

    fun addHabit(habit: Habit) {
        val newHabit = habit.copy(id = UUID.randomUUID().toString(),)
        val currentList = _habits.value.toMutableList()
        currentList.add(newHabit)
        _habits.value = currentList
        viewModelScope.launch {
            repository.addHabit(newHabit)
            if (newHabit.time.isNotBlank()) {
                NotificationUtils.scheduleNotification(context, newHabit)
            }
        }
    }

    fun updateHabit(habit: Habit) {
        val currentList = _habits.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            currentList[index] = habit
            _habits.value = currentList
        }
        viewModelScope.launch {
            repository.updateHabit(habit)
            if (habit.time.isNotBlank()) {
                NotificationUtils.scheduleNotification(context, habit)
            } else {
                NotificationUtils.cancelNotification(context, habit)
            }
        }
    }

    fun deleteHabit(id: String) {
        val currentList = _habits.value.toMutableList()
        val habit = currentList.find { it.id == id }
        currentList.removeAll { it.id == id }
        _habits.value = currentList
        viewModelScope.launch {
            repository.deleteHabit(id)
            habit?.let { NotificationUtils.cancelNotification(context, it) }
        }
    }

    fun toggleCompleted(id: String, date: LocalDate) {
        val currentList = _habits.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val habit = currentList[index]
            val newCompletedDates = if (habit.completedDates.contains(date)) {
                habit.completedDates - date
            } else {
                habit.completedDates + date
            }
            val updatedHabit = habit.copy(
                completedDates = newCompletedDates,
                streak = if (newCompletedDates.contains(date)) habit.streak + 1 else habit.streak - 1
            )
            currentList[index] = updatedHabit
            _habits.value = currentList
            viewModelScope.launch {
                repository.updateHabit(updatedHabit)
            }
        }
    }

    fun getHabitById(id: String): Habit? {
        return _habits.value.find { it.id == id }
    }

    fun getWeeklyProgress(habit: Habit): Float {
        val today = LocalDate.now()
        val weekStart = today.minusDays(6)
        val completedInWeek = habit.completedDates.count { date ->
            !date.isBefore(weekStart) && !date.isAfter(today)
        }
        val dueInWeek = (0..6).count {
            isHabitDueOnDate(habit, today.minusDays(it.toLong()))
        }
        return if (dueInWeek > 0) completedInWeek.toFloat() / dueInWeek else 0f
    }
}