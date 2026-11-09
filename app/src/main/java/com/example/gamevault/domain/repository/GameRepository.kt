package com.example.gamevault.domain.repository

// Imports de FILES tipo CLASS
import com.example.gamevault.domain.model.Videojuego
import com.example.gamevault.domain.model.VideojuegoDraft

interface GameRepository {

    // 	Obtener todos los videojuegos del usuario.
    suspend fun getGames(): List<Videojuego>

    // 	Obtener un videojuego específico.
    suspend fun getGameById(id: Int): Videojuego

    // Crear un videojuego nuevo.
    suspend fun createGame(game: VideojuegoDraft): Videojuego

    // 	Actualizar uno existente.
    suspend fun updateGame(
        id: Int,
        game: VideojuegoDraft
    ): Videojuego

    // Eliminar un videojuego.
    suspend fun deleteGame(id: Int)
}