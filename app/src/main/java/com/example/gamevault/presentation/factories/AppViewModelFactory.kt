package com.example.gamevault.presentation.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gamevault.di.AppContainer
import com.example.gamevault.presentation.auth.viewmodels.AuthViewModel
import com.example.gamevault.presentation.games.viewmodels.GameViewModel

class AppViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(appContainer.authRepository) as T
            }

            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(appContainer.gameRepository) as T
            }

            else -> throw IllegalArgumentException(
                "ViewModel desconocido: ${modelClass.name}"
            )
        }
    }
}