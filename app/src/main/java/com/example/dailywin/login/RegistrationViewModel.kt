package com.example.dailywin.login

import androidx.lifecycle.ViewModel
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
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onLastNameChange(lastName: String) {
        _uiState.value = _uiState.value.copy(lastName = lastName)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun createAccount() {
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
