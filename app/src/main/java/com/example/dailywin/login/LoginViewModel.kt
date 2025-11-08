package com.example.dailywin.login

import androidx.lifecycle.ViewModel
import com.example.dailywin.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class LoginViewModel : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    private val _signInResult = MutableStateFlow<Result<FirebaseUser?>>(Result.success(null))
    val signInResult: StateFlow<Result<FirebaseUser?>> = _signInResult

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    private fun validate(): Boolean {
        val currentState = _uiState.value
        val emailError = if (currentState.email.isBlank()) R.string.error_field_required else null
        val passwordError = if (currentState.password.isBlank()) R.string.error_field_required else null

        _uiState.value = currentState.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }

    fun signIn() {
        if (!validate()) {
            return
        }

        val email = _uiState.value.email
        val password = _uiState.value.password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInResult.value = Result.success(auth.currentUser)
                } else {
                    _signInResult.value = Result.failure(task.exception ?: Exception("Sign-in failed"))
                }
            }
    }
}
