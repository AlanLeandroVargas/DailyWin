package com.example.dailywin.home

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.dailywin.R
import com.example.dailywin.data.model.Frequency
import com.example.dailywin.data.model.Habit
import com.example.dailywin.data.model.Priority
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHabitScreen(
    habit: Habit? = null,
    onSave: (Habit) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(habit?.name ?: "") }
    var category by remember { mutableStateOf(habit?.category ?: "") }
    var description by remember { mutableStateOf(habit?.description ?: "") }
    var time by remember { mutableStateOf(habit?.time ?: "") }
    var selectedPriority by remember { mutableStateOf(habit?.priority ?: Priority.MEDIUM) }
    var selectedFrequency by remember { mutableStateOf(habit?.frequency ?: Frequency.DAILY) }
    var selectedDays by remember { mutableStateOf(habit?.daysOfWeek ?: emptyList()) }
    var startDate by remember { mutableStateOf(habit?.startDate ?: LocalDate.now()) }
    var endDate by remember { mutableStateOf(habit?.endDate ?: LocalDate.now()) }
    var dailyGoal by remember { mutableStateOf(habit?.dailyGoal ?: "") }
    var additionalGoal by remember { mutableStateOf(habit?.additionalGoal ?: "") }
    var imageUri by remember { mutableStateOf(habit?.imageUri ?: "") }
    var location by remember { mutableStateOf(habit?.location ?: "") }
    var showMapDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val categories = stringArrayResource(id = R.array.habit_categories)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (habit == null) stringResource(id = R.string.new_habit) else stringResource(id = R.string.edit_habit),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                val user = FirebaseAuth.getInstance().currentUser
                                val habitToSave = Habit(
                                    id = habit?.id ?: "",
                                    userId = user?.uid ?: "",
                                    name = name,
                                    category = category,
                                    description = description,
                                    time = time,
                                    reminders = if (time.isNotBlank()) listOf(time) else emptyList(),
                                    priority = selectedPriority,
                                    frequency = selectedFrequency,
                                    startDate = startDate,
                                    endDate = endDate,
                                    dailyGoal = dailyGoal,
                                    additionalGoal = additionalGoal,
                                    streak = habit?.streak ?: 0,
                                    daysOfWeek = selectedDays,
                                    imageUri = imageUri,
                                    location = location
                                )
                                onSave(habitToSave)
                            }
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.save),
                            tint = if (name.isNotBlank())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
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
            SectionTitle(text = stringResource(id = R.string.basic_information))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.habit_name_required)) },
                placeholder = { Text(stringResource(id = R.string.habit_name_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(id = R.string.category),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        CategoryChip(
                            category = cat,
                            selected = category == cat,
                            onClick = { category = cat },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.drop(3).forEach { cat ->
                        CategoryChip(
                            category = cat,
                            selected = category == cat,
                            onClick = { category = cat },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SectionTitle(text = stringResource(id = R.string.priority))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Priority.values().forEach { priority ->
                    PriorityChip(
                        priority = priority,
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            SectionTitle(text = stringResource(id = R.string.frequency))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Frequency.values().forEach { freq ->
                    FrequencyChip(
                        frequency = freq,
                        selected = selectedFrequency == freq,
                        onClick = { selectedFrequency = freq },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (selectedFrequency == Frequency.WEEKLY) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(text = stringResource(id = R.string.days_of_the_week))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val days = stringArrayResource(id = R.array.days_of_week_short)
                    days.forEach { day ->
                        DayOfWeekChip(
                            day = day,
                            selected = selectedDays.contains(day),
                            onClick = {
                                selectedDays = if (selectedDays.contains(day)) {
                                    selectedDays - day
                                } else {
                                    selectedDays + day
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SectionTitle(text = stringResource(id = R.string.period))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.start_date)) },
                    placeholder = { Text(stringResource(id = R.string.select)) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val activity = context as? Activity ?: return@IconButton
                                if (activity.isFinishing || activity.isDestroyed) return@IconButton
                                val calendar = Calendar.getInstance()
                                DatePickerDialog(
                                    activity,
                                    { _, year, month, day ->
                                        startDate = LocalDate.of(year, month + 1, day)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = stringResource(id = R.string.select_date))
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.end_date)) },
                    placeholder = { Text(stringResource(id = R.string.optional)) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val activity = context as? Activity ?: return@IconButton
                                if (activity.isFinishing || activity.isDestroyed) return@IconButton
                                val calendar = Calendar.getInstance()
                                DatePickerDialog(
                                    activity,
                                    { _, year, month, day ->
                                        endDate = LocalDate.of(year, month + 1, day)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = stringResource(id = R.string.select_date))
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            SectionTitle(text = stringResource(id = R.string.reminder))
            OutlinedTextField(
                value = time,
                onValueChange = {},
                label = { Text(stringResource(id = R.string.time)) },
                placeholder = { Text(stringResource(id = R.string.select_time)) },
                readOnly = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val activity = context as? Activity ?: return@IconButton
                            if (activity.isFinishing || activity.isDestroyed) return@IconButton
                            val calendar = Calendar.getInstance()
                            TimePickerDialog(
                                activity,
                                { _, hour, minute ->
                                    time = LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("HH:mm"))
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            ).show()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = stringResource(id = R.string.select_time))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            SectionTitle(text = stringResource(id = R.string.goals))
            OutlinedTextField(
                value = dailyGoal,
                onValueChange = { dailyGoal = it },
                label = { Text(stringResource(id = R.string.daily_goal)) },
                placeholder = { Text(stringResource(id = R.string.daily_goal_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = additionalGoal,
                onValueChange = { additionalGoal = it },
                label = { Text(stringResource(id = R.string.additional_goal)) },
                placeholder = { Text(stringResource(id = R.string.additional_goal_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            SectionTitle(text = stringResource(id = R.string.notes))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.description)) },
                placeholder = { Text(stringResource(id = R.string.description_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            SectionTitle(text = stringResource(id = R.string.extras))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showMapDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = stringResource(id = R.string.select_location))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (location.isNotBlank())
                            stringResource(id = R.string.location_selected)
                        else
                            stringResource(id = R.string.add_location)
                    )
                }
            }
            if (showMapDialog) {
                LocationPickerDialog(
                    onDismiss = { showMapDialog = false },
                    onLocationSelected = { lat, lon ->
                        location = "$lat,$lon"
                        showMapDialog = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun DayOfWeekChip(
    day: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = day,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun PriorityChip(
    priority: Priority,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (priority) {
        Priority.HIGH -> androidx.compose.ui.graphics.Color(0xFFE53935)
        Priority.MEDIUM -> androidx.compose.ui.graphics.Color(0xFFFB8C00)
        Priority.LOW -> androidx.compose.ui.graphics.Color(0xFF43A047)
    }

    val label = when (priority) {
        Priority.HIGH -> stringResource(id = R.string.priority_high)
        Priority.MEDIUM -> stringResource(id = R.string.priority_medium)
        Priority.LOW -> stringResource(id = R.string.priority_low)
    }

    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) backgroundColor else androidx.compose.ui.graphics.Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = backgroundColor
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) androidx.compose.ui.graphics.Color.White else backgroundColor
            )
        }
    }
}

@Composable
private fun CategoryChip(
    category: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = category,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FrequencyChip(
    frequency: Frequency,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = when (frequency) {
        Frequency.DAILY -> stringResource(id = R.string.frequency_daily)
        Frequency.WEEKLY -> stringResource(id = R.string.frequency_weekly)
        Frequency.MONTHLY -> stringResource(id = R.string.frequency_monthly)
    }

    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LocationPickerDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (Double, Double) -> Unit,
    initialLatitude: Double? = null,
    initialLongitude: Double? = null
) {
    val context = LocalContext.current
    var selectedPoint by remember {
        mutableStateOf(
            if (initialLatitude != null && initialLongitude != null)
                GeoPoint(initialLatitude, initialLongitude)
            else
                null
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            OutlinedButton(
                onClick = {
                    selectedPoint?.let {
                        onLocationSelected(it.latitude, it.longitude)
                    }
                },
                enabled = selectedPoint != null
            ) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        title = { Text(stringResource(id = R.string.select_a_location)) },
        text = {
            AndroidView(
                factory = {
                    MapView(context).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)

                        val startPoint = selectedPoint ?: GeoPoint(-34.6037, -58.3816) // Default: Buenos Aires
                        controller.setCenter(startPoint)

                        selectedPoint?.let { geoPoint ->
                            val marker = Marker(this)
                            marker.position = geoPoint
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            marker.title = context.getString(R.string.location_selected)
                            overlays.add(marker)
                        }

                        overlays.add(object : org.osmdroid.views.overlay.Overlay() {
                            override fun onSingleTapConfirmed(e: android.view.MotionEvent, mapView: MapView): Boolean {
                                val proj = mapView.projection
                                val geoPoint = proj.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                                selectedPoint = geoPoint

                                mapView.overlays.removeAll { it is Marker }
                                val marker = Marker(mapView)
                                marker.position = geoPoint
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = context.getString(R.string.location_selected)
                                mapView.overlays.add(marker)
                                mapView.invalidate()
                                return true
                            }
                        })
                    }
                },
                update = { mapView ->
                    selectedPoint?.let { geoPoint ->
                        mapView.overlays.removeAll { it is Marker }
                        val marker = Marker(mapView)
                        marker.position = geoPoint
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = context.getString(R.string.location_selected)
                        mapView.overlays.add(marker)
                        mapView.controller.setCenter(geoPoint)
                        mapView.invalidate()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    )
}
