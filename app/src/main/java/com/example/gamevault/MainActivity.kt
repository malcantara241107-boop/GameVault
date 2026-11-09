package com.example.gamevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamevault.di.AppContainer
import com.example.gamevault.presentation.auth.viewmodels.AuthViewModel
import com.example.gamevault.presentation.factories.AppViewModelFactory
import com.example.gamevault.presentation.games.viewmodels.GameViewModel
import com.example.gamevault.presentation.navigation.GameVaultNavigation
import com.example.gamevault.ui.theme.GameVaultTheme

class MainActivity : ComponentActivity() {

    private val appContainer: AppContainer
        get() = (application as GameVaultApplication).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModelFactory = AppViewModelFactory(appContainer)

        setContent {
            GameVaultTheme {
                val authViewModel: AuthViewModel = viewModel(
                    factory = viewModelFactory
                )

                val gameViewModel: GameViewModel = viewModel(
                    factory = viewModelFactory
                )

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    GameVaultNavigation(
                        authViewModel = authViewModel,
                        gameViewModel = gameViewModel
                    )
                }
            }
        }
    }
}
