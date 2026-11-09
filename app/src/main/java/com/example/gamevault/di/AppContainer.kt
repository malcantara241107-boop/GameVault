package com.example.gamevault.di

import android.content.Context
import com.example.gamevault.data.local.SessionManager
import com.example.gamevault.data.local.TokenProvider
import com.example.gamevault.data.remote.ApiClient
import com.example.gamevault.data.repository.AuthRepositoryImpl
import com.example.gamevault.data.repository.GameRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val applicationScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    // Persistencia de sesión
    val sessionManager = SessionManager(appContext)

    // Proveedor de token en memoria
    private val tokenProvider = TokenProvider()

    // Cliente HTTP y servicios API
    private val apiClient = ApiClient {
        tokenProvider.getToken()
    }

    val authApi = apiClient.authApi
    val gameApi = apiClient.gameApi

    // Repositorios
    val authRepository = AuthRepositoryImpl(
        authApi = authApi,
        sessionManager = sessionManager
    )

    val gameRepository = GameRepositoryImpl(
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