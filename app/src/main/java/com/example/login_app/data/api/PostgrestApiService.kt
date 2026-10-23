package com.example.login_app.data.api

import com.example.login_app.data.api.dto.LogAccesoRequest
import com.example.login_app.data.api.dto.LogAccesoResponse
import com.example.login_app.data.api.dto.RostroRequest
import com.example.login_app.data.api.dto.RostroResponse
import com.example.login_app.data.api.dto.VerificarRostroRequest
import com.example.login_app.data.api.dto.VerificarRostroResponse
import com.example.login_app.data.api.dto.VistaLogAcceso
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PostgrestApiService {
    @POST("rostros_facial")
    suspend fun guardarRostro(
        @Header("Prefer") prefer: String = "return=representation",
        @Header("Authorization") bearerToken: String, // Requerido para que PostgREST valide el JWT
        @Body request: RostroRequest
    ): Response<List<RostroResponse>> // PostgREST siempre devuelve listas

    @POST("rpc/verificar_rostro")
    suspend fun verificarRostro(
        @Header("Authorization") bearerToken: String,
        @Body body: VerificarRostroRequest
    ): Response<List<VerificarRostroResponse>>

    @POST("logs_acceso")
    suspend fun guardarLog(
        @Header("Prefer") prefer: String = "return=representation",
        @Header("Authorization") bearerToken: String,
        @Body request: LogAccesoRequest
    ): Response<List<LogAccesoResponse>>

    @GET("vista_logs_acceso")
    suspend fun obtenerHistorial(
        @Header("Authorization") bearerToken: String,
        @Query("order") order: String = "accedido_en.desc",
        @Query("limit") limit: Int = 50
    ): Response<List<VistaLogAcceso>>
}