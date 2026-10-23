package com.example.login_app.domain.usecase

import com.example.login_app.data.api.RetrofitClient
import com.example.login_app.data.api.dto.LoginRequest  // ← OJO: .dto
import com.example.login_app.data.api.dto.LoginResponse // ← OJO: .dto
import retrofit2.Response

class LoginUseCase {
    suspend operator fun invoke(correo: String, password: String): Response<LoginResponse> {
        return RetrofitClient.fastApiService.login(LoginRequest(correo, password))
    }
}