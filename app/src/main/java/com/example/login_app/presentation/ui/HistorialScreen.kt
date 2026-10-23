package com.example.login_app.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login_app.data.api.RetrofitClient
import com.example.login_app.data.api.dto.VistaLogAcceso
import com.example.login_app.data.local.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    val token = prefsManager.getSavedToken() ?: ""
    val scope = rememberCoroutineScope()

    var logs by remember { mutableStateOf<List<VistaLogAcceso>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.postgrestApiService.obtenerHistorial(
                    bearerToken = "Bearer $token"
                )
                if (response.isSuccessful) {
                    logs = response.body().orEmpty()
                } else {
                    error = "HTTP ${response.code()}: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                error = e.message ?: "Error desconocido"
            } finally {
                cargando = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Accesos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 24.sp)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Column(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("❌ $error")
                    Spacer(Modifier.height(16.dp))
                }
                logs.isEmpty() -> Text("Sin registros", Modifier.align(Alignment.Center))
                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(logs, key = { it.id }) { log ->
                        ListItem(
                            headlineContent = { Text(log.usuario) },
                            supportingContent = {
                                Text(
                                    "${log.metodo} · ${if (log.exitoso) "✅" else "❌"} ${log.detalle ?: ""}"
                                )
                            },
                            trailingContent = {
                                Text(
                                    log.accedidoEn
                                        .take(19)
                                        .replace("T", " ")
                                )
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}