package com.example.login_app.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.login_app.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToFaceRegister: (nombre: String, apellido: String, correo: String, password: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Registro de Usuario", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.nombreState,
            onValueChange = { viewModel.nombreState = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.apellidoState,
            onValueChange = { viewModel.apellidoState = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.correoState,
            onValueChange = { viewModel.correoState = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.passwordState,
            onValueChange = { viewModel.passwordState = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.validateAndProceed {
                    onNavigateToFaceRegister(
                        viewModel.nombreState,
                        viewModel.apellidoState,
                        viewModel.correoState,
                        viewModel.passwordState
                    )
                }
            }
        ) { Text("Siguiente: Registro Facial") }

        viewModel.errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = msg, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onBackToLogin) {
            Text("Volver al Login")
        }
    }
}