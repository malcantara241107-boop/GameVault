package com.example.gamevault.domain.model

import java.time.LocalDate

data class Videojuego(
    val id: Int,
    val title: String,
    val description: String?,
    val platforms: List<String>,
    val developer: String,
    val genre: String,
    val releaseYear: Int,
    val isOwned: Boolean,
    val obtainedDate: LocalDate?,
    val isCompleted: Boolean,
    val completedDate: LocalDate?,
    val rating: Float?
)