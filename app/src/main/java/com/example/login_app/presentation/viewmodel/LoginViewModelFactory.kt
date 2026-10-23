package com.example.login_app.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.login_app.data.local.PreferencesManager

class LoginViewModelFactory(
    private val prefs: PreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(prefs) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}