package com.example.login_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import com.example.login_app.data.biometric.BiometricHelper
import com.example.login_app.data.local.PreferencesManager
import com.example.login_app.presentation.ui.LoginScreen
import com.example.login_app.presentation.viewmodel.LoginViewModel

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = PreferencesManager(applicationContext)
        val viewModel = LoginViewModel(prefs)
        val biometricHelper = BiometricHelper(this)

        setContent {
            MaterialTheme {
                Surface {
                    LoginScreen(
                        viewModel = viewModel,
                        onTriggerBiometric = {
                            if (biometricHelper.isBiometricAvailable()) {
                                biometricHelper.promptBiometricAuth(
                                    onSuccess = {
                                        // Cargar credenciales almacenadas localmente en cifrado y autenticar
                                        val savedPass = prefs.getSavedPassword()
                                        if (savedPass != null) {
                                            viewModel.passwordState = savedPass
                                            viewModel.authenticateWithApi {
                                                Toast.makeText(this, "Autenticación biométrica correcta", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onError = { err ->
                                        viewModel.statusMessage = err
                                    }
                                )
                            } else {
                                viewModel.statusMessage = "Biometría o PIN de bloqueo no configurado en el teléfono."
                            }
                        },
                        onLoginSuccess = {
                            // Flujo a ejecutar tras login exitoso (ej. transición a registro de rostros o panel)
                            Toast.makeText(this, "Acceso concedido a la API", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}