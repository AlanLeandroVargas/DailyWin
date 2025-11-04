package com.example.dailywin.data.repository

import com.example.dailywin.data.firebase.FirebaseDataSource
import com.example.dailywin.data.model.Habit

class HabitRepository(private val firebaseDataSource: FirebaseDataSource) {
    suspend fun addHabit(habit: Habit) = firebaseDataSource.addHabit(habit)
    suspend fun getHabits(): List<Habit> = firebaseDataSource.getHabits()
    suspend fun deleteHabit(habitId: String) = firebaseDataSource.deleteHabit(habitId)
    suspend fun updateHabit(habit: Habit) = firebaseDataSource.updateHabit(habit)
    fun signOut() = firebaseDataSource.signOut()
}