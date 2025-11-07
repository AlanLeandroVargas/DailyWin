package com.example.dailywin

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.dailywin.data.firebase.FirebaseDataSource
import com.example.dailywin.data.repository.HabitRepository
import com.example.dailywin.home.HabitViewModel
import com.example.dailywin.navigation.AppNavGraph
import com.example.dailywin.navigation.Screen
import com.example.dailywin.ui.LanguageRepository
import com.example.dailywin.ui.theme.DailyWinTheme
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.config.Configuration
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var languageRepository: LanguageRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm_prefs", MODE_PRIVATE))
        languageRepository = LanguageRepository(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
        requestNotificationPermission()
        requestLocationPermission()
        createNotificationChannel()
        setContent {
            val language by languageRepository.language.collectAsState(initial = "en")

            // Apply locale immediately
            updateLocale(language)
            DailyWinTheme {
                DailyWinApp()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            }
        }
    }
    private fun requestLocationPermission() {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE_LOCATION
            )
        }
    }

    companion object {
        private const val REQUEST_CODE_NOTIFICATIONS = 1001
        private const val REQUEST_CODE_LOCATION = 1002
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

fun Context.updateLocale(language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
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
    val currentUser = FirebaseAuth.getInstance().currentUser

    AppNavGraph(
        navController = navController,
        habitViewModel = habitViewModel,
        onHabitCreated = { habit ->
            habitViewModel.addHabit(habit)
        },
        startDestination = if (currentUser != null) Screen.Home.route else Screen.Login.route
    )
}
