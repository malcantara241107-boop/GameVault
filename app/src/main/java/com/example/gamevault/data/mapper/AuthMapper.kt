// Este Mapper se encargará de convertir los DTO de autenticación en modelos de dominio.
package com.example.gamevault.data.mapper

import com.example.gamevault.data.dto.AuthResponseDto
import com.example.gamevault.data.dto.UserDto
import com.example.gamevault.domain.model.User
import com.example.gamevault.domain.model.UserSession

// Convierte un DTO de usuario en un modelo de dominio.
fun UserDto.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email
    )
}

// Convierte la respuesta de autenticación en una sesión.
fun AuthResponseDto.toDomain(): UserSession {
    return UserSession(
        token = token,
        user = user.toDomain()
    )
}