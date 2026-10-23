package com.example.login_app.data.api.dto

import com.google.gson.annotations.SerializedName

data class LogAccesoRequest(
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("metodo") val metodo: String,        // "facial" | "rfid" | "manual" | "password"
    @SerializedName("exitoso") val exitoso: Boolean,
    @SerializedName("detalle") val detalle: String? = null
)

data class LogAccesoResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("usuario_id") val usuarioId: String?,
    @SerializedName("metodo") val metodo: String,
    @SerializedName("exitoso") val exitoso: Boolean,
    @SerializedName("detalle") val detalle: String?,
    @SerializedName("accedido_en") val accedidoEn: String
)
data class VistaLogAcceso(
    @SerializedName("id") val id: Long,
    @SerializedName("usuario_id") val usuarioId: String?,
    @SerializedName("usuario") val usuario: String,
    @SerializedName("metodo") val metodo: String,
    @SerializedName("exitoso") val exitoso: Boolean,
    @SerializedName("detalle") val detalle: String?,
    @SerializedName("accedido_en") val accedidoEn: String
)