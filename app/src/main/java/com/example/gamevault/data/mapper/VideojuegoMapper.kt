package com.example.gamevault.data.mapper

import com.example.gamevault.data.dto.VideojuegoDto
import com.example.gamevault.data.dto.VideojuegoRequestDto
import com.example.gamevault.domain.model.Videojuego
import com.example.gamevault.domain.model.VideojuegoDraft
import java.time.LocalDate

// Convierte un DTO recibido desde la API en un modelo de dominio.
fun VideojuegoDto.toDomain(): Videojuego {
    return Videojuego(
        id = id,
        title = title,
        description = description,
        platforms = platforms,
        developer = developer,
        genre = genre,
        releaseYear = releaseYear,
        isOwned = isOwned,
        obtainedDate = obtainedDate?.let {
            LocalDate.parse(it)
        },
        isCompleted = isCompleted,
        completedDate = completedDate?.let {
            LocalDate.parse(it)
        },
        rating = rating
    )
}

// Convierte un borrador de videojuego en un DTO para la API.
fun VideojuegoDraft.toRequestDto(): VideojuegoRequestDto {
    return VideojuegoRequestDto(
        title = title,
        description = description,
        platforms = platforms,
        developer = developer,
        genre = genre,
        releaseYear = releaseYear,
        isOwned = isOwned,
        obtainedDate = obtainedDate?.toString(),
        isCompleted = isCompleted,
        completedDate = completedDate?.toString(),
        rating = rating
    )
}