package com.example.dailywin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.dailywin.data.firebase.FirebaseDataSource
import com.example.dailywin.data.repository.HabitRepository
import com.example.dailywin.home.HabitViewModel
import com.example.dailywin.navigation.AppNavGraph
import com.example.dailywin.ui.theme.DailyWinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            DailyWinTheme {
                DailyWinApp()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Habit Reminders"
            val descriptionText = "Channel for habit reminder notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("habit_reminders", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

class HabitViewModelFactory(
    private val repository: HabitRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun DailyWinApp() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current

    val firebaseDataSource = FirebaseDataSource()
    val repository = HabitRepository(firebaseDataSource)
    val factory = HabitViewModelFactory(repository, context)

    val habitViewModel: HabitViewModel = viewModel(factory = factory)

    AppNavGraph(
        navController = navController,
        habitViewModel = habitViewModel,
        onHabitCreated = { habit ->
            habitViewModel.addHabit(habit)
        }
    )
}