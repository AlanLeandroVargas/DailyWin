package com.example.dailywin

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
        setContent {
            DailyWinTheme {
                DailyWinApp()
            }
        }
    }
}

class HabitViewModelFactory(
    private val repository: HabitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun DailyWinApp() {
    val navController = rememberNavController()

    val firebaseDataSource = FirebaseDataSource()
    val repository = HabitRepository(firebaseDataSource)
    val factory = HabitViewModelFactory(repository)

    val habitViewModel: HabitViewModel = viewModel(factory = factory)

    AppNavGraph(
        navController = navController,
        habitViewModel = habitViewModel,
        onHabitCreated = { habit ->
            habitViewModel.addHabit(habit)
        }
    )
}