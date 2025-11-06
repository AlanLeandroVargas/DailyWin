package com.example.dailywin.home

import java.time.LocalTime
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywin.MainActivity
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

    private val _dueTodayHabits = MutableStateFlow<List<Habit>>(emptyList())
    val dueTodayHabits: StateFlow<List<Habit>> = _dueTodayHabits.asStateFlow()

    private val _notDueTodayHabits = MutableStateFlow<List<Habit>>(emptyList())
    val notDueTodayHabits: StateFlow<List<Habit>> = _notDueTodayHabits.asStateFlow()

    private val _completedHabits = MutableStateFlow<List<Habit>>(emptyList())
    val completedHabits: StateFlow<List<Habit>> = _completedHabits.asStateFlow()


    init {
        loadInitialData()
//
//        val intent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val builder = NotificationCompat.Builder(context, "habit_reminders")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setContentTitle("My notification")
//            .setContentText("Hello World!")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        with(NotificationManagerCompat.from(context)) {
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                NotificationManagerCompat.from(context).notify(1, builder.build())
//            }
//        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val initialHabits = repository.getHabits().map { it.copy(streak = calculateStreak(it) { today }) }
            _habits.value = initialHabits
            initialHabits.forEach {
                val localTime = LocalTime.parse(it.time) // LocalTime.of(11, 45)
                val hour = localTime.hour   // 11
                val minute = localTime.minute // 45
                NotificationUtils.scheduleNotification(
                    context = context,
                    hour = hour,
                    minute = minute,
                    title = "Daily Win",
                    message = it.name
                )
            }

            _dueTodayHabits.value = initialHabits
                .filter { isHabitDueOnDate(it, today) && !it.endDate.isBefore(today) }
                .sortedByDescending { it.priority }

            _notDueTodayHabits.value = initialHabits
                .filter { !isHabitDueOnDate(it, today) && !it.endDate.isBefore(today) }

            _completedHabits.value = initialHabits
                .filter { it.endDate.isBefore(today) }
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
//            if (newHabit.time.isNotBlank()) {
//                NotificationUtils.scheduleNotification(context, newHabit)
//            }
            loadInitialData()
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
//            if (habit.time.isNotBlank()) {
//                NotificationUtils.scheduleNotification(context, habit)
//            } else {
//                NotificationUtils.cancelNotification(context, habit)
//            }
            loadInitialData()
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            val habit = _habits.value.find { it.id == id }
            repository.deleteHabit(id)
//            habit?.let { NotificationUtils.cancelNotification(context, it) }
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

    private fun calculateStreak(habit: Habit, clock: () -> LocalDate = { LocalDate.now() }): Int {
        val completedDates = habit.completedDates
        if (completedDates.isEmpty()) {
            return 0
        }

        var streak = 0
        val today = clock()
        var currentDate = today

        while (currentDate >= habit.startDate) {
            if (isHabitDueOnDate(habit, currentDate)) {
                if (currentDate == today && !completedDates.contains(currentDate)) {
                    currentDate = currentDate.minusDays(1)
                    continue
                }
                if (completedDates.contains(currentDate)) {
                    streak++
                } else {
                    break
                }
            }

            currentDate = currentDate.minusDays(1)
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

    fun calculateTotalDueDays(habit: Habit): Int {
        var totalDueDays = 0
        var currentDate = habit.startDate
        val endDate = LocalDate.now()

        while (!currentDate.isAfter(endDate)) {
            if (isHabitDueOnDate(habit, currentDate)) {
                totalDueDays++
            }
            currentDate = currentDate.plusDays(1)
        }
        return totalDueDays
    }

    fun signOut() {
        repository.signOut()
    }
}
