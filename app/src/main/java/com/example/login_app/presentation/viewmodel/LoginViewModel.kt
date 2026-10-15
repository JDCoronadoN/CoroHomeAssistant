package com.example.login_app.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login_app.data.api.LoginRequest
import com.example.login_app.data.api.RetrofitClient
import com.example.login_app.data.local.PreferencesManager
import kotlinx.coroutines.launch

class LoginViewModel(private val prefs: PreferencesManager) : ViewModel() {

    var usernameState by mutableStateOf("")
    var passwordState by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var statusMessage by mutableStateOf<String?>(null)
    var isAuthSuccessful by mutableStateOf(false)

    init {
        usernameState = prefs.getSavedUsername() ?: ""
    }
    fun clearFields() {
        usernameState = ""
        passwordState = ""
        statusMessage = null
        isAuthSuccessful = false
        isLoading = false
    }
    fun authenticateWithApi(onSuccess: () -> Unit) {
        if (usernameState.isBlank() || passwordState.isBlank()) {
            statusMessage = "Por favor, complete correo y contraseña."
            return
        }

        viewModelScope.launch {
            isLoading = true
            statusMessage = null
            try {
                val response = RetrofitClient.rostroApiService.login(
                    LoginRequest(correo = usernameState, password = passwordState)
                )

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.access_token
                    prefs.saveCredentials(usernameState, passwordState) // O guardar el token JWT
                    isAuthSuccessful = true
                    statusMessage = "¡Conexión Exitosa, Bienvenido!"
                    onSuccess()
                } else {
                    statusMessage = "Credenciales incorrectas o usuario no encontrado."
                }
            } catch (e: Exception) {
                statusMessage = "Error de red/API: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
    fun authenticateAuto(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val savedUser = prefs.getSavedUsername()
        val savedPass = prefs.getSavedPassword()

        if (savedUser.isNullOrEmpty() || savedPass.isNullOrEmpty()) {
            onError("No hay credenciales guardadas")
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // Ahora validamos llamando al endpoint /login de FastAPI
                val response = RetrofitClient.rostroApiService.login(
                    LoginRequest(correo = savedUser, password = savedPass)
                )

                if (response.isSuccessful && response.body() != null) {
                    // Si el servidor valida el usuario y contraseña, el login automático es exitoso
                    onSuccess()
                } else {
                    onError("Las credenciales guardadas ya no son válidas")
                }
            } catch (e: Exception) {
                onError("Error de red: ${e.localizedMessage}")
            } finally {
                isLoading = false // Corregido: se detiene el indicador de carga
            }
        }
    }
}