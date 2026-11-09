package com.example.gamevault.data.repository

import com.example.gamevault.data.dto.LoginRequestDto
import com.example.gamevault.data.dto.RegisterRequestDto
import com.example.gamevault.data.local.SessionManager
import com.example.gamevault.data.mapper.toDomain
import com.example.gamevault.data.remote.AuthApi
import com.example.gamevault.domain.model.UserSession
import com.example.gamevault.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): UserSession {

        val request = RegisterRequestDto(
            name = name,
            email = email,
            password = password
        )

        val session = authApi.register(request).toDomain()

        sessionManager.saveSession(session)

        return session
    }

    override suspend fun login(
        email: String,
        password: String
    ): UserSession {

        val request = LoginRequestDto(
            email = email,
            password = password
        )

        val session = authApi.login(request).toDomain()

        sessionManager.saveSession(session)

        return session
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override suspend fun getCurrentSession(): UserSession? {
        return sessionManager.session.first()
    }
}