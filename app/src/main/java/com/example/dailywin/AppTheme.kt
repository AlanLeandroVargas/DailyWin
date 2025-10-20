package com.example.dailywin

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme()  // Default dark colors
    } else {
        lightColorScheme() // Default light colors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography, // use defaults
        shapes = MaterialTheme.shapes,         // use defaults
        content = content
    )
}