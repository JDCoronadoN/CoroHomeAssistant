package com.example.login_app.domain.usecase

import android.graphics.BitmapFactory
import com.example.login_app.data.api.PostgrestApiService
import com.example.login_app.data.api.dto.VerificarRostroRequest
import com.example.login_app.data.api.dto.VerificarRostroResponse
import com.example.login_app.domain.embeddings.FaceNet
import com.example.login_app.domain.face_detection.MLKitFaceDetector

class VerifyFaceUseCase(
    private val faceDetector: MLKitFaceDetector,
    private val faceNet: FaceNet,
    private val postgrestApi: PostgrestApiService
) {
    suspend fun invoke(imageBytes: ByteArray, token: String): Result<VerificarRostroResponse?> {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return Result.failure(Exception("Imagen inválida"))

        val croppedFace = faceDetector.getCroppedFaceFromBitmap(bitmap).getOrNull()
            ?: return Result.failure(Exception("No se detectó rostro"))

        val embedding = faceNet.getFaceEmbedding(croppedFace)

        return try {
            val response = postgrestApi.verificarRostro(
                bearerToken = "Bearer $token",
                body = VerificarRostroRequest(queryVector = embedding.toList())
            )

            if (response.isSuccessful) {
                val resultado = response.body()?.firstOrNull()
                Result.success(resultado) // null si no hay coincidencias
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}