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
//        val initialHabits = listOf(
//            Habit(
//                id = UUID.randomUUID().toString(),
//                name = "Ejercicio matutino",
//                category = "Salud",
//                description = "Hacer 30 minutos de ejercicio cardiovascular",
//                time = "07:00",
//                priority = Priority.HIGH,
//                frequency = Frequency.DAILY,
//                startDate = "2025-01-01",
//                dailyGoal = "30 minutos",
//                additionalGoal = "Mejorar resistencia cardiovascular",
//                completed = false,
//                streak = 15
//            ),
//            Habit(
//                id = UUID.randomUUID().toString(),
//                name = "Leer antes de dormir",
//                category = "Aprendizaje",
//                description = "Lectura de 20 páginas diarias",
//                time = "22:00",
//                priority = Priority.MEDIUM,
//                frequency = Frequency.DAILY,
//                startDate = "2025-01-15",
//                dailyGoal = "20 páginas",
//                additionalGoal = "Completar 12 libros al año",
//                completed = true,
//                streak = 8
//            )
//        )
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
            repository.addHabit(habit)
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