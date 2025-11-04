package com.example.dailywin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dailywin.data.model.Habit
import com.example.dailywin.home.CalendarScreen
import com.example.dailywin.home.EditHabitScreen
import com.example.dailywin.home.HabitDetailScreen
import com.example.dailywin.home.HabitViewModel
import com.example.dailywin.home.HomeScreen
import com.example.dailywin.home.NewHabitScreen
import com.example.dailywin.home.StatsScreen
import com.example.dailywin.login.LoginScreen
import com.example.dailywin.login.RegistrationScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registration : Screen("registration")
    object Home : Screen("home")
    object CreateHabit : Screen("create_habit")
    object EditHabit : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: String) = "edit_habit/$habitId"
    }
    object HabitDetail : Screen("habit_detail/{habitId}") {
        fun createRoute(habitId: String) = "habit_detail/$habitId"
    }
    object Calendar : Screen("calendar")
    object Stats : Screen("stats")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    habitViewModel: HabitViewModel,
    onHabitCreated: (Habit) -> Unit,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                }
            )
        }

        composable(Screen.Registration.route) {
            RegistrationScreen(
                onRegistrationSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = habitViewModel,
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateHabit.route)
                },
                onNavigateToDetail = { habitId ->
                    navController.navigate(Screen.HabitDetail.createRoute(habitId))
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route)
                },
                onNavigateToStats = {
                    navController.navigate(Screen.Stats.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateHabit.route) {
            NewHabitScreen(
                habit = null,
                onSave = { habit ->
                    onHabitCreated(habit)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditHabit.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
            val habit = habitViewModel.getHabitById(habitId)

            if (habit != null) {
                EditHabitScreen(
                    habit = habit,
                    onSave = { updatedHabit ->
                        habitViewModel.updateHabit(updatedHabit)
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                viewModel = habitViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { habitId: String ->
                    navController.navigate(Screen.EditHabit.createRoute(habitId))
                }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                viewModel = habitViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
            val habit = habitViewModel.getHabitById(habitId)

            if (habit != null) {
                HabitDetailScreen(
                    habit = habit,
                    onNavigateToEdit = {
                        navController.navigate(Screen.EditHabit.createRoute(habitId))
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    viewModel = habitViewModel
                )
            }
        }
    }
}