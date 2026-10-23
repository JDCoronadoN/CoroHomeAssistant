package com.example.login_app.domain.usecase

import android.graphics.BitmapFactory
import com.example.login_app.data.api.FastApiService
import com.example.login_app.data.api.PostgrestApiService
import com.example.login_app.data.api.dto.LoginRequest
import com.example.login_app.data.api.dto.RegisterRequest
import com.example.login_app.data.api.dto.RostroRequest
import com.example.login_app.domain.embeddings.FaceNet
import com.example.login_app.domain.face_detection.MLKitFaceDetector

class RegisterFaceUseCase(
    private val faceDetector: MLKitFaceDetector,
    private val faceNet: FaceNet,
    private val postgrestApi: PostgrestApiService,
    private val fastApi: FastApiService
) {
    /**
     * Registro completo: crea usuario, hace login, guarda rostro.
     */
    suspend fun registerNewUserWithFace(
        nombre: String, apellido: String, correo: String, password: String,
        imageBytes: ByteArray
    ): Result<Boolean> {
        // 1. Registrar usuario en FastAPI
        val registerResponse = fastApi.register(
            RegisterRequest(nombre, apellido, correo, password)
        )
        if (!registerResponse.isSuccessful || registerResponse.body() == null) {
            return Result.failure(Exception("Error registrando usuario: ${registerResponse.code()}"))
        }
        val nuevoUsuario = registerResponse.body()!!

        // 2. Login automático para obtener token
        val loginResponse = fastApi.login(LoginRequest(correo, password))
        if (!loginResponse.isSuccessful || loginResponse.body() == null) {
            return Result.failure(Exception("Error en login: ${loginResponse.code()}"))
        }
        val token = loginResponse.body()!!.accessToken

        // 3. Procesar rostro
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return Result.failure(Exception("Imagen inválida"))
        val croppedFace = faceDetector.getCroppedFaceFromBitmap(bitmap).getOrNull()
            ?: return Result.failure(Exception("No se detectó rostro"))
        val embedding = faceNet.getFaceEmbedding(croppedFace)

        // 4. Guardar rostro en PostgREST
        val rostroResponse = postgrestApi.guardarRostro(
            bearerToken = "Bearer $token",
            request = RostroRequest(
                usuarioId = nuevoUsuario.usuarioId,
                vectorEmbedding = embedding.toList()
            )
        )

        return if (rostroResponse.isSuccessful) {
            Result.success(true)
        } else {
            Result.failure(Exception("Error guardando rostro: ${rostroResponse.code()}"))
        }
    }

    /**
     * Para usuarios ya logueados que añaden más rostros.
     */
    suspend fun registerFaceForExistingUser(
        imageBytes: ByteArray, usuarioId: String, token: String
    ): Result<Boolean> {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return Result.failure(Exception("Imagen inválida"))
        val croppedFace = faceDetector.getCroppedFaceFromBitmap(bitmap).getOrNull()
            ?: return Result.failure(Exception("No se detectó rostro"))
        val embedding = faceNet.getFaceEmbedding(croppedFace)

        val response = postgrestApi.guardarRostro(
            bearerToken = "Bearer $token",
            request = RostroRequest(usuarioId, embedding.toList())
        )
        return if (response.isSuccessful) Result.success(true)
        else Result.failure(Exception("HTTP ${response.code()}"))
    }
}
fun parseVector(raw: String): List<Float> {
    return raw.trim().removeSurrounding("[", "]")
        .split(",")
        .map { it.trim().toFloat() }
}