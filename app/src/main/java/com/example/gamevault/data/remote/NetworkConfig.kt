// Centraliza la configuración del formato JSON que utilizará Retrofit
package com.example.gamevault.data.remote

import kotlinx.serialization.json.Json

object NetworkConfig {

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = false
        explicitNulls = true
    }
}