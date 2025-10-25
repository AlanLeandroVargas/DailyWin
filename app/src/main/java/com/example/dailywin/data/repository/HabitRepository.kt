package com.example.dailywin.data.repository

import com.example.dailywin.data.firebase.FirebaseDataSource
import com.example.dailywin.data.model.Habit

class HabitRepository(private val firebaseDataSource: FirebaseDataSource) {
    suspend fun addHabit(habit: Habit) = firebaseDataSource.addHabit(habit)
}