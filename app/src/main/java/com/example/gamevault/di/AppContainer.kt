package com.example.gamevault.di

import android.content.Context
import com.example.gamevault.data.local.SessionManager
import com.example.gamevault.data.local.TokenProvider
import com.example.gamevault.data.remote.ApiClient
import com.example.gamevault.data.remote.AuthApi
import com.example.gamevault.data.remote.GameApi
import com.example.gamevault.data.repository.AuthRepositoryImpl
import com.example.gamevault.data.repository.GameRepositoryImpl
import com.example.gamevault.domain.repository.AuthRepository
import com.example.gamevault.domain.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val applicationScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    // Persistencia de sesión (DataStore / SharedPreferences detrás de abstracción)
    val sessionManager: SessionManager = SessionManager(appContext)

    // Proveedor de token en memoria para OkHttp Interceptor
    val tokenProvider: TokenProvider = TokenProvider()

    // Cliente HTTP que centraliza OkHttp y Retrofit
    val apiClient: ApiClient = ApiClient {
        tokenProvider.getToken()
    }

    val okHttpClient: OkHttpClient get() = apiClient.okHttpClient
    val retrofit: Retrofit get() = apiClient.retrofit

    val authApi: AuthApi = apiClient.authApi
    val gameApi: GameApi = apiClient.gameApi

    // Repositorios expuestos explícitamente tipados como interfaces de domain
    val authRepository: AuthRepository = AuthRepositoryImpl(
        authApi = authApi,
        sessionManager = sessionManager,
        tokenProvider = tokenProvider
    )

    val gameRepository: GameRepository = GameRepositoryImpl(
        gameApi = gameApi
    )

    init {
        applicationScope.launch {
            sessionManager.session.collect { session ->
                tokenProvider.updateToken(session?.token)
            }
        }
    }
}
