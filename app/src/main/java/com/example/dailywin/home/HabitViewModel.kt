package com.example.dailywin.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywin.data.model.Frequency
import com.example.dailywin.data.model.Habit
import com.example.dailywin.data.model.Priority
import com.example.dailywin.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private var idCounter = 1L

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            var initialHabits = repository.getHabits()
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
        }
    }

    fun deleteHabit(id: String) {
        val currentList = _habits.value.toMutableList()
        currentList.removeAll { it.id == id }
        _habits.value = currentList
        viewModelScope.launch {
            repository.deleteHabit(id)
        }
    }

    fun toggleCompleted(id: String) {
        val currentList = _habits.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val habit = currentList[index]
            val newCompleted = !habit.completed
            currentList[index] = habit.copy(
                completed = newCompleted,
                streak = if (newCompleted) habit.streak + 1 else 0
            )
            _habits.value = currentList
        }
    }

    fun getHabitById(id: String): Habit? {
        return _habits.value.find { it.id == id }
    }
}