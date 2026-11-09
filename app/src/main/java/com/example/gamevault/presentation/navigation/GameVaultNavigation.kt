package com.example.gamevault.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import com.example.gamevault.presentation.auth.viewmodels.AuthViewModel
import com.example.gamevault.presentation.games.viewmodels.GameViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun GameVaultNavigation(
    authViewModel: AuthViewModel,
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authState.isCheckingSession, authState.session) {
        if (!authState.isCheckingSession) {

            val destination = if (authState.session != null) {
                GameVaultRoutes.HOME
            } else {
                GameVaultRoutes.LOGIN
            }

            if (
                navController.currentDestination != null &&
                navController.currentDestination?.route != destination
            ) {
                navController.navigate(destination) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    if (authState.isCheckingSession) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (authState.session != null) {
                GameVaultRoutes.HOME
            } else {
                GameVaultRoutes.LOGIN
            },
            modifier = modifier
        ) {
            composable(GameVaultRoutes.LOGIN) {
                // LoginScreen
            }

            composable(GameVaultRoutes.REGISTER) {
                // RegisterScreen
            }

            composable(GameVaultRoutes.HOME) {
                // HomeScreen
            }
        }
    }
}