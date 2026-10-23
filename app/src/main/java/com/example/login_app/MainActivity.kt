package com.example.login_app

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.login_app.presentation.ui.NotificationHelper
import com.example.login_app.presentation.viewmodel.LoginViewModel
import com.example.login_app.presentation.viewmodel.LoginViewModelFactory
import com.example.login_app.ui.theme.Login_APPTheme

class MainActivity : FragmentActivity() {
    private lateinit var prefsManager: PreferencesManager
    private lateinit var viewModel: LoginViewModel
    private lateinit var biometricHelper: BiometricHelper
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Aquí puedes manejar opcionalmente si el usuario concedió o denegó el permiso
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsManager = PreferencesManager(this)
        val factory = LoginViewModelFactory(prefsManager)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        biometricHelper = BiometricHelper(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            Login_APPTheme {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {

                    if (viewModel.shouldCheckAutoLogin()) {

                        biometricHelper.promptBiometricAuth(
                            onSuccess = {
                                viewModel.authenticateAuto(
                                    onSuccess = {
                                        val usuarioActual = prefsManager.getSavedUsername() ?: "Usuario"
                                        NotificationHelper.mostrarNotificacionBienvenida(this@MainActivity, usuarioActual)
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    },
                                    onError = { errorMsg ->
                                        Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            onError = { errString ->
                                Toast.makeText(this@MainActivity, errString, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
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