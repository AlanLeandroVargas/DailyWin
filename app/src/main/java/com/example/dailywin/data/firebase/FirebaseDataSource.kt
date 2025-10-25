package com.example.dailywin.data.firebase

import com.example.dailywin.data.model.Habit
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val habitsCollection = firestore.collection("habits")

    suspend fun addHabit(habit: Habit){
        habitsCollection.document(habit.id).set(habit).await();
    }

//    suspend fun addUser(user: User) {
//        usersCollection.document(user.id).set(user).await()
//    }
//
//    suspend fun getUsers(): List<User> {
//        val snapshot = usersCollection.get().await()
//        return snapshot.toObjects(User::class.java)
//    }
//
//    suspend fun updateUser(user: User) {
//        usersCollection.document(user.id).set(user).await()
//    }
//
//    suspend fun deleteUser(userId: String) {
//        usersCollection.document(userId).delete().await()
//    }
}