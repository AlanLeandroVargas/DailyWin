package com.example.dailywin.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegistrationViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsState()
    val signUpResult by viewModel.signUpResult.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(signUpResult) {
        signUpResult.let { result ->
            result.onSuccess { user ->
                if (user != null) {
                    onRegistrationSuccess()
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
                value = state.value.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Name") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = state.value.lastName,
                onValueChange = { viewModel.onLastNameChange(it) },
                label = { Text("Last Name") }
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                viewModel.createAccount()
            }) {
                Text("Register")
            }
            ClickableText(
                text = AnnotatedString("¿Ya tenes cuenta? Inicia sesión desde acá"),
                onClick = { onNavigateToLogin() },
                modifier = Modifier.padding(top = 12.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}
