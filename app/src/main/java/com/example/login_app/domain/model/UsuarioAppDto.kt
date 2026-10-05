package com.example.login_app.data.model

import com.google.gson.annotations.SerializedName

data class UsuarioAppDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("usuario_id")
    val usuarioId: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("password_hash")
    val passwordHash: String,

    @SerializedName("activo")
    val activo: Boolean,

    @SerializedName("creado_en")
    val creadoEn: String? = null
)