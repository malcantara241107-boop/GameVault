package com.example.gamevault.presentation.games.viewmodels

import androidx.lifecycle.ViewModel
import com.example.gamevault.domain.model.Videojuego
import com.example.gamevault.domain.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.example.gamevault.domain.model.VideojuegoDraft
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update

data class GameUiState(
    val games: List<Videojuego> = emptyList(),
    val selectedGame: Videojuego? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class GameViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun loadGames() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                val games = gameRepository.getGames()

                _uiState.update {
                    it.copy(
                        games = games,
                        isLoading = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "No se pudieron cargar los videojuegos"
                    )
                }
            }
        }
    }

    fun loadGameById(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    selectedGame = null
                )
            }

            try {
                val game = gameRepository.getGameById(id)

                _uiState.update {
                    it.copy(
                        selectedGame = game,
                        isLoading = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "No se pudo obtener el videojuego"
                    )
                }
            }
        }
    }

    fun createGame(game: VideojuegoDraft) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null
                )
            }

            try {
                val createdGame = gameRepository.createGame(game)

                _uiState.update {
                    it.copy(
                        games = it.games + createdGame,
                        selectedGame = createdGame,
                        isLoading = false,
                        successMessage = "Videojuego creado correctamente"
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "No se pudo crear el videojuego"
                    )
                }
            }
        }
    }

    fun updateGame(id: Int, game: VideojuegoDraft) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null
                )
            }

            try {
                val updatedGame = gameRepository.updateGame(id, game)

                _uiState.update {
                    it.copy(
                        games = it.games.map { currentGame ->
                            if (currentGame.id == id) updatedGame
                            else currentGame
                        },
                        selectedGame = updatedGame,
                        isLoading = false,
                        successMessage = "Videojuego actualizado correctamente"
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "No se pudo actualizar el videojuego"
                    )
                }
            }
        }
    }

    fun deleteGame(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null
                )
            }

            try {
                gameRepository.deleteGame(id)

                _uiState.update {
                    it.copy(
                        games = it.games.filter { game ->
                            game.id != id
                        },
                        selectedGame = if (it.selectedGame?.id == id) {
                            null
                        } else {
                            it.selectedGame
                        },
                        isLoading = false,
                        successMessage = "Videojuego eliminado correctamente"
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "No se pudo eliminar el videojuego"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    fun clearSuccessMessage() {
        _uiState.update {
            it.copy(successMessage = null)
        }
    }
}