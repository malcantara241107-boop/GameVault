package com.example.gamevault.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamevault.presentation.auth.screens.LoginScreen
import com.example.gamevault.presentation.auth.screens.RegisterScreen
import com.example.gamevault.presentation.auth.viewmodels.AuthViewModel
import com.example.gamevault.presentation.games.screens.GameDetailScreen
import com.example.gamevault.presentation.games.screens.GameFormScreen
import com.example.gamevault.presentation.games.screens.HomeScreen
import com.example.gamevault.presentation.games.viewmodels.GameViewModel

@Composable
fun GameVaultNavigation(
    authViewModel: AuthViewModel,
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val gameState by gameViewModel.uiState.collectAsStateWithLifecycle()

    // Manejo de retroalimentación mediante Snackbars (RF-10)
    LaunchedEffect(gameState.userMessage) {
        gameState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            gameViewModel.clearUserMessage()
        }
    }

    // Navegación automática según sesión activa (RF-03 y RF-04)
    LaunchedEffect(authState.isCheckingSession, authState.session) {
        if (!authState.isCheckingSession) {
            val destination = if (authState.session != null) {
                GameVaultRoutes.HOME
            } else {
                GameVaultRoutes.LOGIN
            }

            navController.navigate(destination) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        if (authState.isCheckingSession) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = if (authState.session != null) GameVaultRoutes.HOME else GameVaultRoutes.LOGIN,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Pantalla de Inicio de Sesión (RF-02)
                composable(GameVaultRoutes.LOGIN) {
                    LoginScreen(
                        state = authState,
                        onEmailChange = authViewModel::onLoginEmailChanged,
                        onPasswordChange = authViewModel::onLoginPasswordChanged,
                        onLoginClick = authViewModel::login,
                        onNavigateToRegister = {
                            authViewModel.clearError()
                            navController.navigate(GameVaultRoutes.REGISTER)
                        }
                    )
                }

                // Pantalla de Registro (RF-01)
                composable(GameVaultRoutes.REGISTER) {
                    RegisterScreen(
                        state = authState,
                        onNameChange = authViewModel::onRegisterNameChanged,
                        onEmailChange = authViewModel::onRegisterEmailChanged,
                        onPasswordChange = authViewModel::onRegisterPasswordChanged,
                        onConfirmPasswordChange = authViewModel::onRegisterConfirmPasswordChanged,
                        onRegisterClick = authViewModel::register,
                        onNavigateToLogin = {
                            authViewModel.clearError()
                            navController.popBackStack()
                        }
                    )
                }

                // Pantalla Principal: CRUD de Videojuegos (RF-04, RF-05, RF-09)
                composable(GameVaultRoutes.HOME) {
                    val session = authState.session
                    if (session != null) {
                        HomeScreen(
                            user = session.user,
                            gameState = gameState,
                            onLogout = {
                                authViewModel.logout()
                            },
                            onLoadGames = {
                                gameViewModel.loadGames()
                            },
                            onGameClick = { gameId ->
                                navController.navigate(GameVaultRoutes.gameDetail(gameId))
                            },
                            onCreateGame = {
                                gameViewModel.initFormForCreate()
                                navController.navigate(GameVaultRoutes.CREATE_GAME)
                            },
                            onEditGame = { game ->
                                gameViewModel.initFormForEdit(game)
                                navController.navigate(GameVaultRoutes.editGame(game.id))
                            },
                            onDeleteGame = { gameId ->
                                gameViewModel.deleteGame(gameId)
                            }
                        )
                    }
                }

                // Pantalla Formulario: Crear Videojuego (RF-07)
                composable(GameVaultRoutes.CREATE_GAME) {
                    GameFormScreen(
                        state = gameState,
                        onTitleChange = gameViewModel::onTitleChanged,
                        onDescriptionChange = gameViewModel::onDescriptionChanged,
                        onDeveloperChange = gameViewModel::onDeveloperChanged,
                        onGenreChange = gameViewModel::onGenreChanged,
                        onReleaseYearChange = gameViewModel::onReleaseYearChanged,
                        onTogglePlatform = gameViewModel::onTogglePlatform,
                        onIsOwnedChange = gameViewModel::onIsOwnedChanged,
                        onObtainedDateChange = gameViewModel::onObtainedDateChanged,
                        onIsCompletedChange = gameViewModel::onIsCompletedChanged,
                        onCompletedDateChange = gameViewModel::onCompletedDateChanged,
                        onRatingChange = gameViewModel::onRatingChanged,
                        onSave = {
                            gameViewModel.submitForm {
                                navController.popBackStack()
                            }
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                // Pantalla Detalle de Videojuego (RF-06)
                composable(
                    route = GameVaultRoutes.GAME_DETAIL,
                    arguments = listOf(navArgument("gameId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val gameId = backStackEntry.arguments?.getInt("gameId") ?: 0
                    val game = gameState.games.find { it.id == gameId } ?: gameState.selectedGame

                    LaunchedEffect(gameId) {
                        if (game == null) {
                            gameViewModel.loadGameById(gameId)
                        }
                    }

                    GameDetailScreen(
                        game = game,
                        isLoading = gameState.isLoading,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onEditGame = { selectedGame ->
                            gameViewModel.initFormForEdit(selectedGame)
                            navController.navigate(GameVaultRoutes.editGame(selectedGame.id))
                        },
                        onDeleteGame = { idToDelete ->
                            gameViewModel.deleteGame(idToDelete) {
                                navController.popBackStack()
                            }
                        }
                    )
                }

                // Pantalla Formulario: Editar Videojuego (RF-08)
                composable(
                    route = GameVaultRoutes.EDIT_GAME,
                    arguments = listOf(navArgument("gameId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val gameId = backStackEntry.arguments?.getInt("gameId") ?: 0

                    LaunchedEffect(gameId) {
                        if (gameState.formId != gameId) {
                            val game = gameState.games.find { it.id == gameId }
                            if (game != null) {
                                gameViewModel.initFormForEdit(game)
                            } else {
                                gameViewModel.loadGameById(gameId)
                            }
                        }
                    }

                    GameFormScreen(
                        state = gameState,
                        onTitleChange = gameViewModel::onTitleChanged,
                        onDescriptionChange = gameViewModel::onDescriptionChanged,
                        onDeveloperChange = gameViewModel::onDeveloperChanged,
                        onGenreChange = gameViewModel::onGenreChanged,
                        onReleaseYearChange = gameViewModel::onReleaseYearChanged,
                        onTogglePlatform = gameViewModel::onTogglePlatform,
                        onIsOwnedChange = gameViewModel::onIsOwnedChanged,
                        onObtainedDateChange = gameViewModel::onObtainedDateChanged,
                        onIsCompletedChange = gameViewModel::onIsCompletedChanged,
                        onCompletedDateChange = gameViewModel::onCompletedDateChanged,
                        onRatingChange = gameViewModel::onRatingChanged,
                        onSave = {
                            gameViewModel.submitForm {
                                navController.popBackStack()
                            }
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
