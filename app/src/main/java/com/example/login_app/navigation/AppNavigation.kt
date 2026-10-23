package com.example.login_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.login_app.data.local.PreferencesManager
import com.example.login_app.presentation.ui.FacialRecognitionScreen
import com.example.login_app.presentation.ui.HistorialScreen
import com.example.login_app.presentation.ui.HomeScreen
import com.example.login_app.presentation.ui.LoginScreen
import com.example.login_app.presentation.ui.RegisterScreen
import com.example.login_app.presentation.ui.VerifyFaceScreen
import com.example.login_app.presentation.viewmodel.LoginViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object FaceRegister : Screen("face_register") // Ruta base; se usan query params
    object Historial : Screen("historial_acceso")
    object VerifyFace : Screen("verify_face")
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
        // --- LOGIN ---
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // --- REGISTRO (solo valida y navega con datos) ---
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // No se usa en el nuevo flujo, pero lo dejamos por compatibilidad
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToFaceRegister = { nombre, apellido, correo, password ->
                    // Navegamos con los datos del usuario como query params
                    navController.navigate(
                        "face_register?nombre=$nombre&apellido=$apellido&correo=$correo&password=$password"
                    )
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // --- HOME ---
        composable(Screen.Home.route) {
            val currentUsername = remember {
                prefsManager.getSavedUsername() ?: "Usuario"
            }

            HomeScreen(
                username = currentUsername,
                prefsManager = prefsManager,
                onVerifyFaceClick = {                      // ← NUEVO
                    navController.navigate(Screen.VerifyFace.route)
                },
                onViewLogsClick = {
                    navController.navigate(Screen.Historial.route)
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

        // --- REGISTRO FACIAL (con parámetros opcionales) ---
        composable(
            route = "face_register?nombre={nombre}&apellido={apellido}&correo={correo}&password={password}&userId={userId}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType; defaultValue = "" },
                navArgument("apellido") { type = NavType.StringType; defaultValue = "" },
                navArgument("correo") { type = NavType.StringType; defaultValue = "" },
                navArgument("password") { type = NavType.StringType; defaultValue = "" },
                navArgument("userId") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            FacialRecognitionScreen(
                nombre = backStackEntry.arguments?.getString("nombre").orEmpty(),
                apellido = backStackEntry.arguments?.getString("apellido").orEmpty(),
                correo = backStackEntry.arguments?.getString("correo").orEmpty(),
                password = backStackEntry.arguments?.getString("password").orEmpty(),
                usuarioId = backStackEntry.arguments?.getString("userId").orEmpty(),
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterComplete = {
                    // Después de registrar, vamos al login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.VerifyFace.route) {
            VerifyFaceScreen(
                onBackClick = { navController.popBackStack() },
                onMatchFound = { nombre ->
                    // Aquí podrías mostrar un diálogo o navegar a otra pantalla
                },
                onNoMatch = {
                    // Mostrar mensaje
                }
            )
        }

        // --- LOGS ---
        composable(Screen.Historial.route) {
            HistorialScreen(onBackClick = { navController.popBackStack() })
        }
    }
}