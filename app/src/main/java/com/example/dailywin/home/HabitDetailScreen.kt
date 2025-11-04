package com.example.dailywin.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailywin.data.model.Habit
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habit: Habit,
    onNavigateToEdit: (String) -> Unit,
    onBack: () -> Unit
) {
    val completionRate = if (habit.completedDates.isNotEmpty()) {
        habit.completedDates.size.toFloat() / (LocalDate.now().toEpochDay() - habit.startDate.toEpochDay() + 1).toFloat()
    } else {
        0f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = habit.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(habit.id) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Hábito"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Estadísticas del Hábito",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Racha actual:", fontWeight = FontWeight.Bold)
                        Text(text = "${habit.streak} días")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Tasa de finalización:", fontWeight = FontWeight.Bold)
                        Text(text = "${(completionRate * 100).toInt()}%")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = completionRate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            if (habit.description.isNotBlank()) {
                Text(text = "Descripción:", fontWeight = FontWeight.Bold)
                Text(text = habit.description)
            }

            CalendarView(completedDates = habit.completedDates)
        }
    }
}

@Composable
fun CalendarView(completedDates: List<LocalDate>) {
    val currentMonth = YearMonth.now()
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    val daysInMonth = currentMonth.lengthOfMonth()
    val daysOfWeek = remember {
        DayOfWeek.values().map { it.getDisplayName(TextStyle.SHORT, Locale("es", "ES")) }
    }

    Column {
        Text(
            text = "Calendario de finalización",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(text = day, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // This is a simplified calendar view. A more robust implementation would use a grid layout.
        val calendarRows = (daysInMonth + firstDayOfWeek - 1) / 7 + 1
        var dayCounter = 1
        repeat(calendarRows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (i in 1..7) {
                    if ((it == 0 && i < firstDayOfWeek) || dayCounter > daysInMonth) {
                        Spacer(modifier = Modifier.size(40.dp))
                    } else {
                        val date = currentMonth.atDay(dayCounter)
                        val isCompleted = completedDates.contains(date)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCompleted) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$dayCounter")
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}
