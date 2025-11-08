package com.example.dailywin.login

import androidx.annotation.StringRes

data class RegistrationUiState(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    @StringRes val nameError: Int? = null,
    @StringRes val lastNameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null
)