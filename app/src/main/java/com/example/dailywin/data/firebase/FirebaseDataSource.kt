package com.example.dailywin.data.firebase

import com.example.dailywin.data.model.Habit
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val habitsCollection = firestore.collection("habits")

    suspend fun addHabit(habit: Habit){
        habitsCollection.document(habit.id).set(habit).await()
    }
    suspend fun getHabits(): List<Habit>{
        val snapshot = habitsCollection.get().await()
        return snapshot.toObjects(Habit::class.java)
    }
    suspend fun updateHabit(habit: Habit){
        habitsCollection.document(habit.id).set(habit).await()
    }

    suspend fun deleteHabit(habitId: String) {
        habitsCollection.document(habitId).delete().await()
    }
}