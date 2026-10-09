package com.example.login_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.login_app.data.biometric.BiometricHelper
import com.example.login_app.data.local.PreferencesManager
import com.example.login_app.presentation.navigation.AppNavigation
import com.example.login_app.presentation.ui.LoginScreen
import com.example.login_app.presentation.viewmodel.LoginViewModel
import com.example.login_app.presentation.viewmodel.LoginViewModelFactory
import com.example.login_app.ui.theme.Login_APPTheme

class MainActivity : FragmentActivity() {
    private lateinit var prefsManager: PreferencesManager
    private lateinit var viewModel: LoginViewModel
    private lateinit var biometricHelper: BiometricHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsManager = PreferencesManager(this)
        val factory = LoginViewModelFactory(prefsManager)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        biometricHelper = BiometricHelper(this)
        setContent {
            Login_APPTheme {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    if (prefsManager.isAutoLoginEnabled()) {
                        biometricHelper.promptBiometricAuth(
                            onSuccess = {
                                // Huella/PIN correcto -> Autenticar con credenciales encriptadas
                                viewModel.authenticateAuto(
                                    onSuccess = {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    },
                                    onError = { errorMsg ->
                                        Toast.makeText(
                                            this@MainActivity,
                                            errorMsg,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            },
                            onError = { errString ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Autenticación biométrica cancelada. Inicie sesión con clave.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
                // Pasamos 'this@MainActivity' que es un FragmentActivity válido
                AppNavigation(
                    navController = navController,
                    activity = this@MainActivity,
                    viewModel= viewModel,
                    prefsManager = prefsManager
                )
            }
        }
    }
}