package com.example.login_app.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login_app.data.api.RetrofitClient
import com.example.login_app.data.local.PreferencesManager
import kotlinx.coroutines.launch

class LoginViewModel(private val prefs: PreferencesManager) : ViewModel() {

    var usernameState by mutableStateOf("")
    var passwordState by mutableStateOf("")
    var rememberState by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var statusMessage by mutableStateOf<String?>(null)
    var isAuthSuccessful by mutableStateOf(false)

    init {
        // Cargar estado inicial si existen credenciales guardadas
        if (prefs.isRememberEnabled()) {
            rememberState = true
            usernameState = prefs.getSavedUsername() ?: ""
        }
    }

    fun authenticateWithApi(onSuccess: () -> Unit) {
        if (usernameState.isBlank() || passwordState.isBlank()) {
            statusMessage = "Por favor, complete usuario y contraseña."
            return
        }

        viewModelScope.launch {
            isLoading = true
            statusMessage = null
            try {
                // Consulta directa a PostgREST en Docker filtrando por usuario
                val response = RetrofitClient.rostroApiService.obtenerUsuario(
                    username = "eq.$usernameState"
                )

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val user = response.body()!!.first()

                    if (user.passwordHash == passwordState) {
                        if (rememberState) {
                            prefs.saveCredentials(usernameState, passwordState)
                        } else {
                            prefs.clearCredentials()
                        }
                        isAuthSuccessful = true
                        statusMessage = "Conexión exitosa. Bienvenido."
                        onSuccess()
                    } else {
                        statusMessage = "Contraseña incorrecta."
                    }
                } else {
                    statusMessage = "Usuario no encontrado."
                }
            } catch (e: Exception) {
                statusMessage = "Error de red/API: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}