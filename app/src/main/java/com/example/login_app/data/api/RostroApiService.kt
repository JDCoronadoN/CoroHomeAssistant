package com.example.login_app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val correo: String, val password: String)
data class LoginResponse(val access_token: String, val token_type: String)
data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String,
    val rol: String = "user"
)
data class RegisterResponse(
    val usuario_id: String,
    val correo: String,
    val rol: String,
    val activo: Boolean
)

interface RostroApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}