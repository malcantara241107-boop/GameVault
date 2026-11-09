package com.example.gamevault.data.repository

import com.example.gamevault.data.dto.LoginRequestDto
import com.example.gamevault.data.dto.RegisterRequestDto
import com.example.gamevault.data.local.SessionManager
import com.example.gamevault.data.local.TokenProvider
import com.example.gamevault.data.mapper.toDomain
import com.example.gamevault.data.remote.AuthApi
import com.example.gamevault.domain.model.UserSession
import com.example.gamevault.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
    private val tokenProvider: TokenProvider? = null
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): UserSession {
        return try {
            val request = RegisterRequestDto(
                name = name,
                email = email,
                password = password
            )
            val session = authApi.register(request).toDomain()
            sessionManager.saveSession(session)
            tokenProvider?.updateToken(session.token)
            session
        } catch (e: Throwable) {
            throw mapThrowable(e)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): UserSession {
        return try {
            val request = LoginRequestDto(
                email = email,
                password = password
            )
            val session = authApi.login(request).toDomain()
            sessionManager.saveSession(session)
            tokenProvider?.updateToken(session.token)
            session
        } catch (e: Throwable) {
            throw mapThrowable(e)
        }
    }

    override suspend fun logout() {
        try {
            authApi.logout()
        } catch (_: Exception) {
            // Ignorar errores del endpoint y garantizar limpieza local
        } finally {
            tokenProvider?.updateToken(null)
            sessionManager.clearSession()
        }
    }

    override suspend fun getCurrentSession(): UserSession? {
        val session = sessionManager.session.first()
        tokenProvider?.updateToken(session?.token)
        return session
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
                    400 -> "Datos incorrectos en la solicitud"
                    401 -> "Credenciales incorrectas"
                    409 -> "El correo electrónico ya está registrado"
                    else -> "Error HTTP ${throwable.code()}: ${throwable.message()}"
                }
                Exception(message)
            }
            is IOException -> Exception("Error de red: Comprueba tu conexión a internet")
            else -> throwable
        }
    }
}
