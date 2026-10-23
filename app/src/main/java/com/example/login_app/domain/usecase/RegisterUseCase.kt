package com.example.login_app.domain.usecase

import com.example.login_app.data.api.RetrofitClient
import com.example.login_app.data.api.dto.RegisterRequest
import com.example.login_app.data.api.dto.RegisterResponse
import retrofit2.Response

class RegisterUseCase {
    suspend operator fun invoke(
        nombre: String,
        apellido: String,
        correo: String,
        password: String
    ): Response<RegisterResponse> {
        return RetrofitClient.fastApiService.register(
            RegisterRequest(
                nombre = nombre,
                apellido = apellido,
                correo = correo,
                password = password
            )
        )
    }
}