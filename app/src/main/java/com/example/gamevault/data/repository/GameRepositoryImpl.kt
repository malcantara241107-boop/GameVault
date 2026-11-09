package com.example.gamevault.data.repository

import com.example.gamevault.data.mapper.toDomain
import com.example.gamevault.data.mapper.toRequestDto
import com.example.gamevault.data.remote.GameApi
import com.example.gamevault.domain.model.Videojuego
import com.example.gamevault.domain.model.VideojuegoDraft
import com.example.gamevault.domain.repository.GameRepository
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import java.io.IOException

class GameRepositoryImpl(
    private val gameApi: GameApi
) : GameRepository {

    override suspend fun getGames(): List<Videojuego> {
        return try {
            gameApi.getGames().map { it.toDomain() }
        } catch (e: Throwable) {
            throw mapThrowable(e)
        }
    }

    override suspend fun getGameById(id: Int): Videojuego {
        return try {
            gameApi.getGameById(id).toDomain()
        } catch (e: Throwable) {
            throw mapThrowable(e)
        }
    }

    override suspend fun createGame(game: VideojuegoDraft): Videojuego {
        return try {
            gameApi.createGame(game.toRequestDto()).toDomain()
        } catch (e: Throwable) {
            throw mapThrowable(e)
        }
    }

    override suspend fun updateGame(
        id: Int,
        game: VideojuegoDraft
    ): Videojuego {
        return try {
            gameApi.updateGame(id, game.toRequestDto()).toDomain()
        } catch (e: Throwable) {
            throw mapThrowable(e)
        }
    }

    override suspend fun deleteGame(id: Int) {
        try {
            gameApi.deleteGame(id)
        } catch (e: Throwable) {
            throw mapThrowable(e)
        }
    }

    private fun mapThrowable(throwable: Throwable): Throwable {
        return when (throwable) {
            is HttpException -> {
                val message = try {
                    val errorBody = throwable.response()?.errorBody()?.string()
                    if (!errorBody.isNullOrBlank()) {
                        val json = kotlinx.serialization.json.Json.parseToJsonElement(errorBody)
                        json.jsonObject["message"]?.jsonPrimitive?.contentOrNull
                    } else null
                } catch (_: Exception) {
                    null
                } ?: when (throwable.code()) {
                    400 -> "Datos del videojuego inválidos"
                    401 -> "Sesión expirada o no autorizada"
                    404 -> "Videojuego no encontrado"
                    else -> "Error HTTP ${throwable.code()}: ${throwable.message()}"
                }
                Exception(message)
            }
            is IOException -> Exception("Error de red: Comprueba tu conexión a internet")
            else -> throwable
        }
    }
}
