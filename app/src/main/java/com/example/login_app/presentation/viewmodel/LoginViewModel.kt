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
            statusMessage = "Por favor, complete usuario y contraseña."
            return
        }

        viewModelScope.launch {
            isLoading = true
            statusMessage = null
            try {
                val response = RetrofitClient.rostroApiService.obtenerUsuario(
                    username = "eq.$usernameState"
                )

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val user = response.body()!!.first()

                    if (user.passwordHash == passwordState) {
                            prefs.saveCredentials(usernameState, passwordState)
                            isAuthSuccessful = true
                            statusMessage = "Conexion Exitosa, Bienvenido"
                            onSuccess()
                    } else {
                        statusMessage = "Contrasena incorrecta."
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
    fun authenticateAuto(onSuccess: () -> Unit, onError: (String) -> Unit){
        val savedUser = prefs.getSavedUsername()
        val savedPass = prefs.getSavedPassword()

        if(savedUser.isNullOrEmpty() || savedPass.isNullOrEmpty()){
            onError("No hay credenciales guardadas")
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.rostroApiService.obtenerUsuario(username = "eq.$savedUser")
                if(response.isSuccessful && !response.body().isNullOrEmpty()){
                    val user = response.body()!!.first()
                    if(user.passwordHash == savedPass){
                        onSuccess()
                    }
                    else{
                        onError("Las credenciales guardadas ya no son validas")
                    }
                }
                else{
                    onError("Error de conexion al validar usuario")
                }
            }
            catch (e: Exception){
                onError("Error de red: ${e.localizedMessage}")
            } finally {
                isLoading = true
            }
        }
    }
}