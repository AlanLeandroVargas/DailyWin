package com.example.dailywin.home

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailywin.data.model.Frequency
import com.example.dailywin.data.model.Habit
import com.example.dailywin.data.model.Priority
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitScreen(
    habit: Habit?,
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
    val categories = listOf("Salud", "Productividad", "Finanzas", "Aprendizaje", "Relaciones", "Hobbies")

    val startDatePicker = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = LocalDate.of(year, month + 1, day)
                startDate = selectedDate
//                startDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val endDatePicker = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = LocalDate.of(year, month + 1, day)
                endDate = selectedDate
//                endDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePicker = remember {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val selectedTime = LocalTime.of(hour, minute)
                time = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (habit == null) "Nuevo hábito" else "Editar hábito",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (name.isNotBlank() && habit != null) {
                                val habitToSave = habit.copy(
                                    name = name,
                                    category = category,
                                    description = description,
                                    time = time,
                                    priority = selectedPriority,
                                    frequency = selectedFrequency,
                                    startDate = startDate,
                                    endDate = endDate,
                                    dailyGoal = dailyGoal,
                                    additionalGoal = additionalGoal,
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
                            contentDescription = "Guardar",
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
            SectionTitle(text = "Información básica")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del hábito *") },
                placeholder = { Text("Ej: Hacer ejercicio") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Categoría",
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

            SectionTitle(text = "Prioridad")
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

            SectionTitle(text = "Frecuencia")
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
                SectionTitle(text = "Días de la semana")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val days = listOf("L", "M", "X", "J", "V", "S", "D")
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

            SectionTitle(text = "Período")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    onValueChange = {},
                    label = { Text("Fecha de inicio") },
                    placeholder = { Text("Seleccionar") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { startDatePicker.show() }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Seleccionar fecha"
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    onValueChange = {},
                    label = { Text("Fecha de fin") },
                    placeholder = { Text("Opcional") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { endDatePicker.show() }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Seleccionar fecha"
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            SectionTitle(text = "Recordatorio")
            OutlinedTextField(
                value = time,
                onValueChange = {},
                label = { Text("Hora") },
                placeholder = { Text("Seleccionar hora") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { timePicker.show() }) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Seleccionar hora"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            SectionTitle(text = "Objetivos")
            OutlinedTextField(
                value = dailyGoal,
                onValueChange = { dailyGoal = it },
                label = { Text("Objetivo diario") },
                placeholder = { Text("Ej: 30 minutos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = additionalGoal,
                onValueChange = { additionalGoal = it },
                label = { Text("Objetivo adicional") },
                placeholder = { Text("Ej: Perder 5kg en 3 meses") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            SectionTitle(text = "Notas")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                placeholder = { Text("Agrega notas sobre este hábito") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            SectionTitle(text = "Extras")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Implement camera logic */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Agregar foto"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar foto")
                }
                OutlinedButton(
                    onClick = { showMapDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Select location")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (location != null || location != "")
                            "Ubicación seleccionada"
                        else
                            "Agregar ubicación"
                    )
                }
            }
            if (showMapDialog) {
                var initialLat: Double?
                var initialLon: Double?

                habit?.location?.split(",")?.let { parts ->
                    if (parts.size == 2) {
                        initialLat = parts[0].toDoubleOrNull()
                        initialLon = parts[1].toDoubleOrNull()
                    } else {
                        initialLat = null
                        initialLon = null
                    }
                } ?: run {
                    initialLat = null
                    initialLon = null
                }
                LocationPickerDialog(
                    onDismiss = { showMapDialog = false },
                    onLocationSelected = { lat, lon ->
                        location = "$lat,$lon"
                        showMapDialog = false
                    },
                    initialLatitude = initialLat,
                    initialLongitude = initialLon
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
        Priority.HIGH -> "Alta"
        Priority.MEDIUM -> "Media"
        Priority.LOW -> "Baja"
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
        Frequency.DAILY -> "Diaria"
        Frequency.WEEKLY -> "Semanal"
        Frequency.MONTHLY -> "Mensual"
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
