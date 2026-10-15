package com.example.login_app.domain.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String,
    @SerializedName("rol") val rol: String = "user"
)

data class RegisterResponse(
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("activo") val activo: Boolean,
    @SerializedName("creado_en") val creadoEn: String? = null
)