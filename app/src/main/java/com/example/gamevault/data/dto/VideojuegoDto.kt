// Representará un videojuego recibido desde el backend, incluyendo su identificador.
package com.example.gamevault.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideojuegoDto(
    val id: Int,
    val title: String,
    val description: String?,
    val platforms: List<String>,
    val developer: String,
    val genre: String,

    @SerialName("release_year")
    val releaseYear: Int,

    @SerialName("is_owned")
    val isOwned: Boolean,

    @SerialName("obtained_date")
    val obtainedDate: String? = null,

    @SerialName("is_completed")
    val isCompleted: Boolean,

    @SerialName("completed_date")
    val completedDate: String? = null,

    val rating: Float? = null
)