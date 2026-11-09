// Centraliza la configuración del formato JSON y red que utilizará Retrofit
package com.example.gamevault.data.remote

import kotlinx.serialization.json.Json

object NetworkConfig {

    // URL base para el backend en Node/Express.
    // 10.0.2.2 es la dirección especial del emulador Android para conectar al localhost de la máquina host.
    const val BASE_URL = "http://10.0.2.2:3000/"

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
    }
}
