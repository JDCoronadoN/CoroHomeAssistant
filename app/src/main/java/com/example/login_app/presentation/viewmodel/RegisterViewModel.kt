package com.example.login_app.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    var nombreState by mutableStateOf("")
    var apellidoState by mutableStateOf("")
    var correoState by mutableStateOf("")
    var passwordState by mutableStateOf("")

    var errorMessage by mutableStateOf<String?>(null)

    fun validateAndProceed(onReady: () -> Unit) {
        if (nombreState.isBlank() || apellidoState.isBlank() || correoState.isBlank() || passwordState.isBlank()) {
            errorMessage = "Por favor, complete todos los campos."
            return
        }
        errorMessage = null
        onReady()
    }
}