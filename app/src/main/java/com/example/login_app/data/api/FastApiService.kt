package com.example.login_app.data.api

import com.example.login_app.data.api.dto.LoginRequest
import com.example.login_app.data.api.dto.LoginResponse
import com.example.login_app.data.api.dto.RegisterRequest
import com.example.login_app.data.api.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FastApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}