package com.example.login_app.data.api.dto

import com.google.gson.annotations.SerializedName

data class RostroRequest(
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("vector_embedding") val vectorEmbedding: List<Float>
)

// PostgREST devuelve una lista de estos objetos al insertar
data class RostroResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("vector_embedding") val vectorEmbedding: String,
    @SerializedName("creado_en") val creadoEn: String
)