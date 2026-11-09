package com.example.gamevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gamevault.ui.theme.GameVaultTheme
import com.example.gamevault.di.AppContainer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamevault.GameVaultApplication
import com.example.gamevault.presentation.factories.AppViewModelFactory
import com.example.gamevault.presentation.auth.viewmodels.AuthViewModel
import com.example.gamevault.presentation.games.viewmodels.GameViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import com.example.gamevault.presentation.navigation.GameVaultNavigation

class MainActivity : ComponentActivity() {

    private val appContainer: AppContainer
        get() = (application as GameVaultApplication).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModelFactory = AppViewModelFactory(appContainer)

        // Aquí utilizaremos appContainer cuando conectemos
        // nuestros ViewModels y la navegación.

        setContent {
            GameVaultTheme {

                val authViewModel: AuthViewModel = viewModel(
                    factory = viewModelFactory
                )

                val gameViewModel: GameViewModel = viewModel(
                    factory = viewModelFactory
                )

                val authState by authViewModel.uiState.collectAsStateWithLifecycle()
                val gameState by gameViewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    GameVaultNavigation(
                        authViewModel = authViewModel,
                        gameViewModel = gameViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GameVaultTheme {
        Greeting("Android")
    }
}
