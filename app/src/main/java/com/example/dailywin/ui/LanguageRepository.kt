package com.example.dailywin.ui

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LanguageRepository(private val context: Context) {
    private val languageKey = stringPreferencesKey("language")

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[languageKey] ?: "es"
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = language
        }
    }
}
