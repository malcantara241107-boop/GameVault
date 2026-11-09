// Representa los datos que enviaremos para registrar un usuario.
package com.example.gamevault.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
    // ConfirmPassword no esta aqui porque: Porque la confirmación es una validación que realizaremos en la aplicación antes de enviar la solicitud. No necesitamos enviar dos veces la contraseña al backend.
)