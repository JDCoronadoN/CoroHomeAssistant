package com.example.login_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.login_app.data.biometric.BiometricHelper
import com.example.login_app.data.local.PreferencesManager
import com.example.login_app.presentation.ui.HomeScreen
import com.example.login_app.presentation.ui.LoginScreen
import com.example.login_app.presentation.viewmodel.LoginViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object FaceRegister : Screen("face_register")
    object LogsAccess : Screen("logs_access")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    activity: FragmentActivity,
    viewModel: LoginViewModel,
    prefsManager: PreferencesManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            val currentUsername = remember {
                prefsManager.getSavedUsername() ?: "Usuario"
            }

            HomeScreen(
                username = currentUsername,
                prefsManager = prefsManager,
                onRegisterFaceClick = {
                    navController.navigate(Screen.FaceRegister.route)
                },
                onViewLogsClick = {
                    navController.navigate(Screen.LogsAccess.route)
                },
                onLogoutClick = {
                    prefsManager.clearCredentials()
                    prefsManager.setAutoLoginEnabled(false)
                    viewModel.clearFields()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Registro Facial (Estructural)
        composable(Screen.FaceRegister.route) {
            // Próxima implementación
        }

        // Pantalla de Logs de Acceso (Estructural)
        composable(Screen.LogsAccess.route) {
            // Próxima implementación
        }
    }
}