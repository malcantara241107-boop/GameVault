package com.example.gamevault.domain.repository

import com.example.gamevault.domain.model.UserSession

interface AuthRepository {

    // 	Registrar un usuario y devolver su sesión.
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): UserSession

    // Iniciar sesión y devolver su sesión.
    suspend fun login(
        email: String,
        password: String
    ): UserSession

    // Cerrar la sesión y eliminar el token almacenado.
    suspend fun logout()

    // Recuperar la sesión guardada, si existe.
    suspend fun getCurrentSession(): UserSession?
}