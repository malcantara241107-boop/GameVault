// Representa los datos necesarios para iniciar sesión.
package com.example.gamevault.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)