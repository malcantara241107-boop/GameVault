// Representa los datos del usuario que recibiremos desde el backend.
package com.example.gamevault.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)