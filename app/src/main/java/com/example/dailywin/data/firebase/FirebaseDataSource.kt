package com.example.dailywin.data.firebase

import com.example.dailywin.data.model.Habit
import com.example.dailywin.data.model.HabitDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FirebaseDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val habitsCollection = firestore.collection("habits")

    suspend fun addHabit(habit: Habit){
        habitsCollection.document(habit.id).set(habit.toHabitDTO()).await()
    }
    suspend fun getHabits(): List<Habit>{
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserUID = currentUser?.uid ?: ""
        val results = habitsCollection.whereEqualTo("userId", currentUserUID).get().await()
        return results.toObjects(HabitDTO::class.java).map { habitDTO ->
            habitDTO.toHabit()
        }
    }
    suspend fun updateHabit(habit: Habit){
        habitsCollection.document(habit.id).set(habit.toHabitDTO()).await()
    }

    suspend fun deleteHabit(habitId: String) {
        habitsCollection.document(habitId).delete().await()
    }
}