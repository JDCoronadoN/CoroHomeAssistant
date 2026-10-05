package com.example.login_app.data.api

import com.example.login_app.data.model.UsuarioAppDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RostroApiService {

    @GET("usuarios_app")
    suspend fun obtenerUsuario(
        @Query("username") username: String,
        @Query("select") select: String = "usuario_id,username,password_hash,activo"
    ): Response<List<UsuarioAppDto>>
}