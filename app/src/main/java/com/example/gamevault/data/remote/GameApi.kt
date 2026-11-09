package com.example.gamevault.data.remote

import com.example.gamevault.data.dto.VideojuegoDto
import com.example.gamevault.data.dto.VideojuegoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GameApi {

    @GET("games")
    suspend fun getGames(): List<VideojuegoDto>

    @GET("games/{id}")
    suspend fun getGameById(
        @Path("id") id: Int
    ): VideojuegoDto

    @POST("games")
    suspend fun createGame(
        @Body request: VideojuegoRequestDto
    ): VideojuegoDto

    @PUT("games/{id}")
    suspend fun updateGame(
        @Path("id") id: Int,
        @Body request: VideojuegoRequestDto
    ): VideojuegoDto

    @DELETE("games/{id}")
    suspend fun deleteGame(
        @Path("id") id: Int
    )
}