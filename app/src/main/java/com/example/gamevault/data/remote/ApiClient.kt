package com.example.gamevault.data.remote

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

class ApiClient(
    tokenProvider: () -> String?
) {

    private val contentType = "application/json".toMediaType()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            AuthInterceptor(tokenProvider)
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://example.com/")
        .client(okHttpClient)
        .addConverterFactory(
            NetworkConfig.json.asConverterFactory(contentType)
        )
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)

    val gameApi: GameApi = retrofit.create(GameApi::class.java)
}