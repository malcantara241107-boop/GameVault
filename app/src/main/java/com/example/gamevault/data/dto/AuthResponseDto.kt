// Es la representación de la respuesta que recibiremos al registrarnos o iniciar sesión
package com.example.gamevault.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val token: String,
    val user: UserDto
)