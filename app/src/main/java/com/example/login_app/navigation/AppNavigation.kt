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
import com.example.login_app.presentation.viewmodel.LoginViewModelFactory

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object FaceRegister : Screen("face_register")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    activity: FragmentActivity
) {
    val context = LocalContext.current
    val biometricHelper = remember(activity) { BiometricHelper(activity) }

    // Instancia de PreferencesManager y Factory
    val prefsManager = remember { PreferencesManager(context) }
    val factory = remember { LoginViewModelFactory(prefsManager) }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) { backStackEntry ->
            // Creamos o recuperamos el ViewModel en el alcance de este destino o del NavHost
            val loginViewModel: LoginViewModel = viewModel(factory = factory)

            LoginScreen(
                viewModel = loginViewModel,
                onTriggerBiometric = {
                    biometricHelper.promptBiometricAuth(
                        onSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onError = { errorMessage -> }
                    )
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            // Leemos el usuario ingresado desde PreferencesManager (o el estado guardado)
            val currentUsername = remember {
                prefsManager.getSavedUsername() ?: "Usuario"
            }

            HomeScreen(
                username = currentUsername,
                onRegisterFaceClick = {
                    navController.navigate(Screen.FaceRegister.route)
                },
                onLogoutClick = {
                    prefsManager.clearCredentials() // Opcional: Limpiar al cerrar sesión
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.FaceRegister.route) {
            // Pantalla de registro facial
        }
    }
}