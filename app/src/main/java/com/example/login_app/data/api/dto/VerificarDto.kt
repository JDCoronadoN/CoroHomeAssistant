package com.example.login_app.data.api.dto

import com.google.gson.annotations.SerializedName

data class VerificarRostroRequest(
    @SerializedName("query_vector") val queryVector: List<Float>,
    @SerializedName("umbral") val umbral: Float = 0.6f
)

data class VerificarRostroResponse(
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("similitud") val similitud: Float
)