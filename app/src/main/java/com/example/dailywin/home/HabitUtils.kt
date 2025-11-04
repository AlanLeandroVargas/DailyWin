package com.example.dailywin.home

import androidx.compose.ui.graphics.Color
import com.example.dailywin.data.model.Priority
import com.example.dailywin.data.model.Frequency
fun getPriorityColor(priority: Priority): Color {
    return when (priority) {
        Priority.HIGH -> Color(0xFFF43688)
        Priority.MEDIUM -> Color(0xFFFF9800)
        Priority.LOW -> Color(0xFF00BCD4)
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Salud" -> Color(0xFF4CAF50)
        "Productividad" -> Color(0xFF2196F3)
        "Finanzas" -> Color(0xFFFF9800)
        "Aprendizaje" -> Color(0xFF9C27B0)
        "Relaciones" -> Color(0xFFE91E63)
        "Hobbies" -> Color(0xFF00BCD4)
        else -> Color(0xFF757575)
    }
}

fun getPriorityLabel(priority: Priority): String {
    return when (priority) {
        Priority.HIGH -> "Alta"
        Priority.MEDIUM -> "Media"
        Priority.LOW -> "Baja"
    }
}

fun getFrequencyLabel(frequency: Frequency): String {
    return when (frequency) {
        Frequency.DAILY -> "Diaria"
        Frequency.WEEKLY -> "Semanal"
        Frequency.MONTHLY -> "Mensual"
    }
}