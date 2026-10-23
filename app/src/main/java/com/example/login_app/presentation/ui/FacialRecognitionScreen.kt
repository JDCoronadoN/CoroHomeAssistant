package com.example.login_app.presentation.ui

import android.Manifest
import android.util.Log
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
import com.example.login_app.domain.usecase.RegisterFaceUseCase
import com.example.login_app.presentation.ui.components.CameraPreview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FacialRecognitionScreen(
    nombre: String,
    apellido: String,
    correo: String,
    password: String,
    usuarioId: String,
    onBackClick: () -> Unit,
    onRegisterComplete: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefsManager = remember { PreferencesManager(context) }

    // Token solo es válido si el usuario ya está logueado
    val token = prefsManager.getSavedToken() ?: ""

    // userId efectivo: si viene del Home, se usa; si no, se extrae del JWT
    val effectiveUserId = remember(usuarioId, token) {
        usuarioId.ifEmpty {
            prefsManager.getSavedUserId()
                ?: com.example.login_app.domain.utils.extractUserIdFromToken(token)
                ?: ""
        }
    }

    Log.d("FacialScreen", "Modo: ${if (effectiveUserId.isEmpty()) "REGISTRO NUEVO" else "USUARIO EXISTENTE"}")
    Log.d("FacialScreen", "usuarioId recibido: '$usuarioId' → efectivo: '$effectiveUserId'")

    val registerFaceUseCase = remember {
        RegisterFaceUseCase(
            faceDetector = MLKitFaceDetector(context),
            faceNet = FaceNet(context),
            postgrestApi = RetrofitClient.postgrestApiService,
            fastApi = RetrofitClient.fastApiService
        )
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CameraPreview(
                    onImageCaptureReady = { capture ->
                        imageCapture = capture
                    }
                )

                // Guía circular
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .border(4.dp, Color.Green, CircleShape)
                )

                Text(
                    text = "Alinea tu rostro dentro del círculo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 120.dp)
                )

                // Botón de captura
                Button(
                    onClick = {
                        scope.launch {
                            isUploading = true
                            uploadMessage = null

                            val imageBytes = imageCapture?.let {
                                FaceCaptureHelper.captureImageBytes(context, it)
                            }

                            if (imageBytes != null) {
                                val result = if (effectiveUserId.isEmpty()) {
                                    // 🆕 Registro completo (usuario + rostro)
                                    registerFaceUseCase.registerNewUserWithFace(
                                        nombre = nombre,
                                        apellido = apellido,
                                        correo = correo,
                                        password = password,
                                        imageBytes = imageBytes
                                    )
                                } else {
                                    // 👤 Añadir rostro a usuario existente
                                    registerFaceUseCase.registerFaceForExistingUser(
                                        imageBytes = imageBytes,
                                        usuarioId = effectiveUserId,
                                        token = token
                                    )
                                }

                                if (result.isSuccess) {
                                    uploadMessage = "✅ Rostro registrado exitosamente."
                                    isSuccess = true
                                    delay(1500)
                                    onRegisterComplete()
                                } else {
                                    uploadMessage = "❌ Error: ${result.exceptionOrNull()?.message}"
                                }
                            } else {
                                uploadMessage = "❌ No se pudo capturar la imagen."
                            }
                            isUploading = false
                        }
                    },
                    enabled = !isUploading && imageCapture != null && !isSuccess,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(if (effectiveUserId.isEmpty()) "Capturar y Completar Registro" else "Capturar y Añadir Rostro")
                    }
                }

                // Mensaje de feedback
                uploadMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = if (msg.startsWith("✅")) Color.Green else Color.Red,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 100.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Se requiere acceso a la cámara frontal para el reconocimiento facial.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Conceder Permiso de Cámara")
                }
            }
        }

        // Botón de regreso
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Text(
                    text = " ← ",
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}