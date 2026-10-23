package com.example.login_app.domain.usecase

import com.example.login_app.data.api.PostgrestApiService
import com.example.login_app.data.api.dto.LogAccesoRequest

class LogAccessUseCase(
    private val postgrestApi: PostgrestApiService
) {
    suspend fun registrar(
        usuarioId: String,
        metodo: String,
        exitoso: Boolean,
        detalle: String? = null,
        token: String
    ): Result<Boolean> = runCatching {
        val response = postgrestApi.guardarLog(
            bearerToken = "Bearer $token",
            request = LogAccesoRequest(
                usuarioId = usuarioId,
                metodo = metodo,
                exitoso = exitoso,
                detalle = detalle
            )
        )
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}")
        }
        true
    }
}