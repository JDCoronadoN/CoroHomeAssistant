package com.example.login_app.data.api.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String,
    val rol: String = "user"
)
data class RegisterResponse(
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("activo") val activo: Boolean,
    @SerializedName("creado_en") val creadoEn: String? = null
)