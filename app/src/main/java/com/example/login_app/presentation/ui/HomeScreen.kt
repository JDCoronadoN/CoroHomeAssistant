package com.example.login_app.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login_app.data.local.PreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    username: String = "Usuario",
    prefsManager: PreferencesManager,
    onVerifyFaceClick: () -> Unit,
    onViewLogsClick: () -> Unit = {},
    onLogoutClick: () -> Unit
) {
    var isAutoLoginChecked by remember { mutableStateOf(prefsManager.isAutoLoginEnabled()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Bienvenido, $username!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sesión iniciada correctamente.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tarjeta de Iniciar Sesión Automático
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Iniciar sesión automático",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Solicita Huella/PIN al abrir la app",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isAutoLoginChecked,
                        onCheckedChange = { checked ->
                            isAutoLoginChecked = checked
                            prefsManager.setAutoLoginEnabled(checked)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onVerifyFaceClick,          // ← callback
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Verificar Identidad", fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onViewLogsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Ver Historial de Accesos",
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 16.sp
                )
            }
        }
    }
}