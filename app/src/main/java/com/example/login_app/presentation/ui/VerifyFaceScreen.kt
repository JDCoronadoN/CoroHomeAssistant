package com.example.login_app.presentation.ui

import android.Manifest
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.login_app.data.api.RetrofitClient
import com.example.login_app.data.local.PreferencesManager
import com.example.login_app.domain.embeddings.FaceNet
import com.example.login_app.domain.face_detection.MLKitFaceDetector
import com.example.login_app.domain.usecase.FaceCaptureHelper
import com.example.login_app.domain.usecase.LogAccessUseCase
import com.example.login_app.domain.usecase.VerifyFaceUseCase
import com.example.login_app.presentation.ui.components.CameraPreview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VerifyFaceScreen(
    onBackClick: () -> Unit,
    onMatchFound: (String) -> Unit,
    onNoMatch: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefsManager = remember { PreferencesManager(context) }
    val token = prefsManager.getSavedToken() ?: ""

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val verifyFaceUseCase = remember {
        VerifyFaceUseCase(
            faceDetector = MLKitFaceDetector(context),
            faceNet = FaceNet(context),
            postgrestApi = RetrofitClient.postgrestApiService
        )
    }
    val logAccessUseCase = remember {
        LogAccessUseCase(RetrofitClient.postgrestApiService)
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isVerifying by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var matchFound by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CameraPreview(onImageCaptureReady = { imageCapture = it })

                Box(
                    Modifier
                        .size(280.dp)
                        .border(4.dp, Color.Blue, CircleShape)
                )

                Text(
                    text = "Alinea tu rostro para verificar",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 120.dp)
                )

                Button(
                    onClick = {
                        scope.launch {
                            isVerifying = true
                            message = null
                            matchFound = false

                            val imageBytes = imageCapture?.let {
                                FaceCaptureHelper.captureImageBytes(context, it)
                            }

                            if (imageBytes != null) {
                                val result = verifyFaceUseCase.invoke(imageBytes, token)

                                result.fold(
                                    onSuccess = { usuario ->
                                        val exitoso = usuario != null
                                        val detalle = if (exitoso) {
                                            "Similitud: ${"%.2f".format(usuario!!.similitud * 100)}%"
                                        } else {
                                            "Sin coincidencias (umbral 0.6)"
                                        }

                                        // Registrar en logs_acceso
                                        logAccessUseCase.registrar(
                                            usuarioId = if (exitoso) usuario!!.usuarioId else prefsManager.getSavedUserId() ?: "",
                                            metodo = "facial",
                                            exitoso = exitoso,
                                            detalle = detalle,
                                            token = token
                                        )

                                        if (exitoso) {
                                            message = "✅ Coincidencia: ${usuario!!.nombre} ${usuario.apellido}"
                                            matchFound = true
                                            onMatchFound("${usuario.nombre} ${usuario.apellido}")
                                        } else {
                                            message = "❌ No se encontraron coincidencias."
                                            onNoMatch()
                                        }
                                    },
                                    onFailure = { e ->
                                        // También registrar el fallo técnico
                                        logAccessUseCase.registrar(
                                            usuarioId = prefsManager.getSavedUserId() ?: "",
                                            metodo = "facial",
                                            exitoso = false,
                                            detalle = "Error técnico: ${e.message}",
                                            token = token
                                        )
                                        message = "❌ Error: ${e.message}"
                                    }
                                )
                            } else {
                                message = "❌ No se pudo capturar la imagen."
                            }
                            isVerifying = false
                        }
                    },
                    enabled = !isVerifying && imageCapture != null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp)
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Verificar Rostro")
                    }
                }

                message?.let {
                    Text(
                        text = it,
                        color = if (matchFound) Color.Green else Color.Red,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 100.dp)
                    )
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Se requiere acceso a la cámara frontal.")
                Spacer(Modifier.height(16.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Conceder Permiso de Cámara")
                }
            }
        }

        // Botón atrás
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.5f)) {
                Text(" ← ", color = Color.White, modifier = Modifier.padding(8.dp))
            }
        }
    }
}