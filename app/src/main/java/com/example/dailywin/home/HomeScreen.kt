package com.example.dailywin.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailywin.data.firebase.FirebaseDataSource
import com.example.dailywin.data.model.Habit
import com.example.dailywin.data.model.Priority
import com.example.dailywin.data.repository.HabitRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
//    viewModel: HabitViewModel = viewModel()
    viewModel: HabitViewModel = HabitViewModel(HabitRepository(FirebaseDataSource()))

) {
    val habits by viewModel.habits.collectAsState()

    var showDetailScreen by remember { mutableStateOf(false) }
    var selectedHabit by remember { mutableStateOf<Habit?>(null) }
    var isEditMode by remember { mutableStateOf(false) }

    if (showDetailScreen) {
        HabitDetailScreen(
            habit = selectedHabit,
            onSave = { habit ->
                if (isEditMode) {
                    viewModel.updateHabit(habit)
                } else {
                    viewModel.addHabit(habit)
                }
                showDetailScreen = false
                selectedHabit = null
            },
            onCancel = {
                showDetailScreen = false
                selectedHabit = null
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = getCurrentDate(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Hoy",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        selectedHabit = null
                        isEditMode = false
                        showDetailScreen = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar hábito"
                    )
                }
            }
        ) { paddingValues ->
            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay hábitos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toca + para crear tu primer hábito",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(habits) { habit ->
                        HabitItemMinimal(
                            habit = habit,
                            onEdit = {
                                selectedHabit = habit
                                isEditMode = true
                                showDetailScreen = true
                            },
                            onDelete = { viewModel.deleteHabit(habit.id) },
                            onToggleCompleted = { viewModel.toggleCompleted(habit.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HabitItemMinimal(
    habit: Habit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleCompleted: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleCompleted() }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        color = if (habit.completed)
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Check Circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (habit.completed)
                            getPriorityColor(habit.priority)
                        else
                            Color.Transparent
                    )
                    .clickable { onToggleCompleted() },
                contentAlignment = Alignment.Center
            ) {
                if (habit.completed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completado",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = "No completado",
                        tint = getPriorityColor(habit.priority),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Habit Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (habit.completed)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (habit.time.isNotBlank()) {
                        Text(
                            text = habit.time,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (habit.category.isNotBlank()) {
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = habit.category,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (habit.streak > 0) {
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${habit.streak} días",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Menu Button
            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más opciones",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (showMenu) {
                    Card(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .width(150.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showMenu = false
                                        onEdit()
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Editar",
                                    fontSize = 14.sp
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showMenu = false
                                        onDelete()
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Eliminar",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getPriorityColor(priority: Priority): Color {
    return when (priority) {
        Priority.HIGH -> Color(0xFFE53935)
        Priority.MEDIUM -> Color(0xFFFB8C00)
        Priority.LOW -> Color(0xFF43A047)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM")
    return today.format(formatter)
}