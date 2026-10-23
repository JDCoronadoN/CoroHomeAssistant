package com.example.login_app.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login_app.data.local.PreferencesManager
import com.example.login_app.domain.usecase.LoginUseCase
import com.example.login_app.domain.utils.extractUserIdFromToken
import kotlinx.coroutines.launch

class LoginViewModel(
    private val prefs: PreferencesManager,
    private val loginUseCase: LoginUseCase = LoginUseCase()
) : ViewModel() {

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
                val response = loginUseCase(usernameState, passwordState)

                if (response.isSuccessful && response.body() != null) {
                    prefs.saveCredentials(usernameState, passwordState)
                    // Después de login exitoso:
                    val loginResponse = response.body()!!
                    prefs.saveToken(loginResponse.accessToken)
                    val userId = extractUserIdFromToken(loginResponse.accessToken)
                    if (userId != null) {
                        prefs.saveUserId(userId)
                    }
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
                val response = loginUseCase(savedUser, savedPass)

                if (response.isSuccessful && response.body() != null) {
                    onSuccess()
                } else {
                    onError("Las credenciales guardadas ya no son válidas")
                }
            } catch (e: Exception) {
                onError("Error de red: ${e.localizedMessage}")
            } finally {
                isLoading = false
            }
        }
    }

    fun shouldCheckAutoLogin(): Boolean {
        return prefs.isAutoLoginEnabled() &&
                !prefs.getSavedUsername().isNullOrEmpty() &&
                !prefs.getSavedPassword().isNullOrEmpty()
    }
}