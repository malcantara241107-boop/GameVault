package com.example.gamevault.data.remote

import com.example.gamevault.data.dto.AuthResponseDto
import com.example.gamevault.data.dto.LoginRequestDto
import com.example.gamevault.data.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): AuthResponseDto

    @POST("login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): AuthResponseDto

    @POST("logout")
    suspend fun logout()
}
