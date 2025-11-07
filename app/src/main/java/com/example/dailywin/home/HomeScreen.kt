package com.example.dailywin.home

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailywin.R
import com.example.dailywin.data.model.Habit
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HabitViewModel,
    onNavigateToCreate: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToEdit: (String) -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val dueTodayHabits by viewModel.dueTodayHabits.collectAsState()
    val notDueTodayHabits by viewModel.notDueTodayHabits.collectAsState()
    val completedHabits by viewModel.completedHabits.collectAsState()
    val today = LocalDate.now()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(id = R.string.daily_win),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = getCurrentDateFormatted(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCalendar) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = stringResource(id = R.string.calendar),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToStats) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = stringResource(id = R.string.statistics),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(id = R.string.options)
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.settings)) },
                                onClick = {
                                    onNavigateToSettings()
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.log_out)) },
                                onClick = {
                                    viewModel.signOut()
                                    onLogout()
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_habit),
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        if (dueTodayHabits.isEmpty() && notDueTodayHabits.isEmpty() && completedHabits.isEmpty()) {
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
                        text = stringResource(id = R.string.no_habits),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.tap_to_create_habit),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                if (dueTodayHabits.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.habits_for_today),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                items(dueTodayHabits) { habit ->
                    HabitItemWithMenu(
                        habit = habit,
                        today = today,
                        onClick = { onNavigateToDetail(habit.id) },
                        onToggleCompleted = { viewModel.toggleCompleted(habit.id, today) },
                        onDelete = { viewModel.deleteHabit(habit.id) },
                        onNavigateToEdit = { onNavigateToEdit(habit.id) }
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }

                if (notDueTodayHabits.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.other_active_habits),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                items(notDueTodayHabits) { habit ->
                    HabitItemWithMenu(
                        habit = habit,
                        today = today,
                        onClick = { onNavigateToDetail(habit.id) },
                        onToggleCompleted = { viewModel.toggleCompleted(habit.id, today) },
                        onDelete = { viewModel.deleteHabit(habit.id) },
                        onNavigateToEdit = { onNavigateToEdit(habit.id) },
                        enabled = false
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }

                if (completedHabits.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.completed_habits),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                items(completedHabits) { habit ->
                    HabitItemWithMenu(
                        habit = habit,
                        today = today,
                        onClick = { onNavigateToDetail(habit.id) },
                        onToggleCompleted = { viewModel.toggleCompleted(habit.id, today) },
                        onDelete = { viewModel.deleteHabit(habit.id) },
                        onNavigateToEdit = { onNavigateToEdit(habit.id) },
                        enabled = false
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun HabitItemWithMenu(
    habit: Habit,
    today: LocalDate,
    onClick: () -> Unit,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    enabled: Boolean = true
) {
    val isCompletedToday = habit.completedDates.contains(today)
    val color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox circular
        if (enabled) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompletedToday)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent
                    )
                    .clickable(enabled = enabled) { onToggleCompleted() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompletedToday) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(id = R.string.completed),
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = stringResource(id = R.string.not_completed),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.width(28.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = habit.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = if (isCompletedToday && enabled)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    color
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (habit.time.isNotBlank()) {
                    Text(
                        text = habit.time,
                        fontSize = 12.sp,
                        color = color
                    )
                }

                if (habit.category.isNotBlank()) {
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = color
                    )
                    Text(
                        text = habit.category,
                        fontSize = 12.sp,
                        color = color
                    )
                }
            }
        }

        // Streak indicator
        if (habit.streak > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (enabled)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${habit.streak}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.options),
                    tint = color
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.edit)) },
                    onClick = {
                        onNavigateToEdit(habit.id)
                        menuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.delete), color = MaterialTheme.colorScheme.error) },
                    onClick = {
                        onDelete()
                        menuExpanded = false
                    }
                )
            }
        }
    }
}

fun getCurrentDateFormatted(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())
    return today.format(formatter).replaceFirstChar { it.uppercase() }
}
