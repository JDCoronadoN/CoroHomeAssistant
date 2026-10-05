package com.example.login_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.login_app.data.biometric.BiometricHelper
import com.example.login_app.data.local.PreferencesManager
import com.example.login_app.presentation.navigation.AppNavigation
import com.example.login_app.presentation.ui.LoginScreen
import com.example.login_app.presentation.viewmodel.LoginViewModel
import com.example.login_app.ui.theme.Login_APPTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Login_APPTheme {
                val navController = rememberNavController()

                // Pasamos 'this@MainActivity' que es un FragmentActivity válido
                AppNavigation(
                    navController = navController,
                    activity = this@MainActivity
                )
            }
        }
    }
}