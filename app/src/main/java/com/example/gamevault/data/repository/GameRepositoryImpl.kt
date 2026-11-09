package com.example.gamevault.data.repository

import com.example.gamevault.data.mapper.toDomain
import com.example.gamevault.data.mapper.toRequestDto
import com.example.gamevault.data.remote.GameApi
import com.example.gamevault.domain.model.Videojuego
import com.example.gamevault.domain.model.VideojuegoDraft
import com.example.gamevault.domain.repository.GameRepository

class GameRepositoryImpl(
    private val gameApi: GameApi
) : GameRepository {

    override suspend fun getGames(): List<Videojuego> {
        return gameApi.getGames().map { it.toDomain() }
    }

    override suspend fun getGameById(id: Int): Videojuego {
        return gameApi.getGameById(id).toDomain()
    }

    override suspend fun createGame(game: VideojuegoDraft): Videojuego {
        return gameApi.createGame(game.toRequestDto()).toDomain()
    }

    override suspend fun updateGame(
        id: Int,
        game: VideojuegoDraft
    ): Videojuego {
        return gameApi.updateGame(id, game.toRequestDto()).toDomain()
    }

    override suspend fun deleteGame(id: Int) {
        gameApi.deleteGame(id)
    }
}