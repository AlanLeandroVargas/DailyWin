package com.example.dailywin.login

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class LoginViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private val _signInResult = MutableStateFlow<Result<FirebaseUser?>>(Result.success(null))
    val signInResult: StateFlow<Result<FirebaseUser?>> = _signInResult

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInResult.value = Result.success(auth.currentUser)
                } else {
                    _signInResult.value = Result.failure(task.exception ?: Exception("Sign-in failed"))
                }
            }
    }

    fun login(): Boolean {
        // Simulate a login check
        return _uiState.value.email.isNotBlank() && _uiState.value.password.isNotBlank()
    }
}