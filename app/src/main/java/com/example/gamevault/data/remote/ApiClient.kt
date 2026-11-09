package com.example.gamevault.data.remote

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(
    baseUrl: String = NetworkConfig.BASE_URL,
    tokenProvider: () -> String?
) {

    private val contentType = "application/json".toMediaType()

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            AuthInterceptor(tokenProvider)
        )
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(
            NetworkConfig.json.asConverterFactory(contentType)
        )
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)

    val gameApi: GameApi = retrofit.create(GameApi::class.java)
}
