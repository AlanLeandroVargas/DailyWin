package com.example.dailywin.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.dailywin.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegistrationViewModel : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private val _signUpResult = MutableStateFlow<Result<FirebaseUser?>>(Result.success(null))
    val signUpResult: StateFlow<Result<FirebaseUser?>> = _signUpResult

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, nameError = null)
    }

    fun onLastNameChange(lastName: String) {
        _uiState.value = _uiState.value.copy(lastName = lastName, lastNameError = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    private fun validatePassword(password: String): Int? {
        if (password.length < 6) {
            return R.string.password_error_length
        }
        if (!password.any { it.isDigit() }) {
            return R.string.password_error_digit
        }
        if (!password.any { it.isUpperCase() }) {
            return R.string.password_error_uppercase
        }
        if (password.all { it.isLetterOrDigit() }) {
            return R.string.password_error_special_char
        }
        return null
    }

    private fun validate(): Boolean {
        val currentState = _uiState.value
        val nameError = if (currentState.name.isBlank()) R.string.error_field_required else null
        val lastNameError = if (currentState.lastName.isBlank()) R.string.error_field_required else null
        val emailError = if (currentState.email.isBlank()) {
            R.string.error_field_required
        } else if (!Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            R.string.error_invalid_email
        } else {
            null
        }
        val passwordError = validatePassword(currentState.password)

        _uiState.value = currentState.copy(
            nameError = nameError,
            lastNameError = lastNameError,
            emailError = emailError,
            passwordError = passwordError
        )

        return nameError == null && lastNameError == null && emailError == null && passwordError == null
    }

    fun createAccount() {
        if (!validate()) {
            return
        }

        val email = _uiState.value.email
        val password = _uiState.value.password
        val name = _uiState.value.name
        val lastName = _uiState.value.lastName

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userMap = hashMapOf(
                            "name" to name,
                            "lastName" to lastName,
                            "email" to email
                        )
                        db.collection("users").document(it.uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                _signUpResult.value = Result.success(user)
                            }
                            .addOnFailureListener { e ->
                                _signUpResult.value = Result.failure(e)
                            }
                    }
                } else {
                    _signUpResult.value = Result.failure(task.exception ?: Exception("Sign-up failed"))
                }
            }
    }
}
