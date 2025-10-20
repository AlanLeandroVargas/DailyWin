package com.example.dailywin.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsState()
    val signInResult by viewModel.signInResult.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(signInResult) {
        signInResult.let { result ->
            result.onSuccess { user ->
                if (user != null) {
                    onLoginSuccess()
                }
            }
            result.onFailure { exception ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = state.value.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = state.value.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                viewModel.signIn()
            }) {
                Text("Login")
            }
            Button(onClick = {
                viewModel.createAccount()
            }){
                Text("Registrarse")
            }
        }
    }
}