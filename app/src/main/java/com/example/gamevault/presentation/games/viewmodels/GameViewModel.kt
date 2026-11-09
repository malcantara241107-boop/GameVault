package com.example.gamevault.presentation.games.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.domain.model.GameConstants
import com.example.gamevault.domain.model.Videojuego
import com.example.gamevault.domain.model.VideojuegoDraft
import com.example.gamevault.domain.repository.GameRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class GameUiState(
    val games: List<Videojuego> = emptyList(),
    val selectedGame: Videojuego? = null,
    val isLoading: Boolean = false,
    val isFormSubmitting: Boolean = false,
    val error: String? = null,
    val userMessage: String? = null,

    // Estado del formulario reutilizable (para Crear y Editar)
    val formId: Int? = null, // null = Modo Crear, no nulo = Modo Editar
    val formTitle: String = "",
    val formDescription: String = "",
    val formDeveloper: String = "",
    val formGenre: String = "",
    val formReleaseYear: String = "",
    val formPlatforms: Set<String> = emptySet(),
    val formIsOwned: Boolean = false,
    val formObtainedDate: LocalDate? = null,
    val formIsCompleted: Boolean = false,
    val formCompletedDate: LocalDate? = null,
    val formRating: Float = 5.0f,

    // Errores de validación en ViewModel (Rúbrica Ítems 2 y 3)
    val formTitleError: String? = null,
    val formDeveloperError: String? = null,
    val formGenreError: String? = null,
    val formReleaseYearError: String? = null,
    val formPlatformsError: String? = null,
    val formDatesError: String? = null,
    val formRatingError: String? = null
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
                        isLoading = false,
                        error = null
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
                it.copy(isLoading = true, error = null, selectedGame = null)
            }

            try {
                val game = gameRepository.getGameById(id)
                _uiState.update {
                    it.copy(
                        selectedGame = game,
                        isLoading = false,
                        error = null
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

    // --- Inicialización del Formulario Reutilizable ---
    fun initFormForCreate() {
        _uiState.update {
            it.copy(
                formId = null,
                formTitle = "",
                formDescription = "",
                formDeveloper = "",
                formGenre = GameConstants.GENRES.firstOrNull() ?: "",
                formReleaseYear = "",
                formPlatforms = emptySet(),
                formIsOwned = false,
                formObtainedDate = null,
                formIsCompleted = false,
                formCompletedDate = null,
                formRating = 5.0f,
                formTitleError = null,
                formDeveloperError = null,
                formGenreError = null,
                formReleaseYearError = null,
                formPlatformsError = null,
                formDatesError = null,
                formRatingError = null,
                isFormSubmitting = false
            )
        }
    }

    fun initFormForEdit(game: Videojuego) {
        _uiState.update {
            it.copy(
                formId = game.id,
                formTitle = game.title,
                formDescription = game.description.orEmpty(),
                formDeveloper = game.developer,
                formGenre = game.genre,
                formReleaseYear = game.releaseYear.toString(),
                formPlatforms = game.platforms.toSet(),
                formIsOwned = game.isOwned,
                formObtainedDate = game.obtainedDate,
                formIsCompleted = game.isCompleted,
                formCompletedDate = game.completedDate,
                formRating = game.rating ?: 5.0f,
                formTitleError = null,
                formDeveloperError = null,
                formGenreError = null,
                formReleaseYearError = null,
                formPlatformsError = null,
                formDatesError = null,
                formRatingError = null,
                isFormSubmitting = false
            )
        }
    }

    // --- Actualizaciones de campos del formulario ---
    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(formTitle = title, formTitleError = null) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(formDescription = description) }
    }

    fun onDeveloperChanged(developer: String) {
        _uiState.update { it.copy(formDeveloper = developer, formDeveloperError = null) }
    }

    fun onGenreChanged(genre: String) {
        _uiState.update { it.copy(formGenre = genre, formGenreError = null) }
    }

    fun onReleaseYearChanged(year: String) {
        _uiState.update { it.copy(formReleaseYear = year, formReleaseYearError = null) }
    }

    fun onTogglePlatform(platform: String) {
        _uiState.update { state ->
            val updated = if (platform in state.formPlatforms) {
                state.formPlatforms - platform
            } else {
                state.formPlatforms + platform
            }
            state.copy(formPlatforms = updated, formPlatformsError = null)
        }
    }

    fun onIsOwnedChanged(isOwned: Boolean) {
        _uiState.update {
            val obtainedDate = if (isOwned) it.formObtainedDate ?: LocalDate.now() else null
            val isCompleted = if (!isOwned) false else it.formIsCompleted
            val completedDate = if (!isOwned) null else it.formCompletedDate
            it.copy(
                formIsOwned = isOwned,
                formObtainedDate = obtainedDate,
                formIsCompleted = isCompleted,
                formCompletedDate = completedDate,
                formDatesError = null
            )
        }
    }

    fun onObtainedDateChanged(date: LocalDate?) {
        _uiState.update { it.copy(formObtainedDate = date, formDatesError = null) }
    }

    fun onIsCompletedChanged(isCompleted: Boolean) {
        _uiState.update {
            val completedDate = if (isCompleted) it.formCompletedDate ?: LocalDate.now() else null
            it.copy(
                formIsCompleted = isCompleted,
                formCompletedDate = completedDate,
                formDatesError = null
            )
        }
    }

    fun onCompletedDateChanged(date: LocalDate?) {
        _uiState.update { it.copy(formCompletedDate = date, formDatesError = null) }
    }

    fun onRatingChanged(rating: Float) {
        _uiState.update { it.copy(formRating = rating, formRatingError = null) }
    }

    // --- Guardado del formulario (Crear / Editar con validaciones en ViewModel) ---
    fun submitForm(onSuccess: () -> Unit) {
        val state = _uiState.value
        val title = state.formTitle.trim()
        val developer = state.formDeveloper.trim()
        val genre = state.formGenre.trim()
        val yearInt = state.formReleaseYear.trim().toIntOrNull()
        val platforms = state.formPlatforms.toList()
        val isOwned = state.formIsOwned
        val obtainedDate = state.formObtainedDate
        val isCompleted = state.formIsCompleted
        val completedDate = state.formCompletedDate
        val rating = state.formRating
        val today = LocalDate.now()

        var hasError = false
        var titleError: String? = null
        var developerError: String? = null
        var genreError: String? = null
        var releaseYearError: String? = null
        var platformsError: String? = null
        var datesError: String? = null

        if (title.isBlank()) {
            titleError = "El título es obligatorio"
            hasError = true
        }

        if (developer.isBlank()) {
            developerError = "El desarrollador es obligatorio"
            hasError = true
        }

        if (genre.isBlank() || genre !in GameConstants.GENRES) {
            genreError = "Selecciona un género válido"
            hasError = true
        }

        if (yearInt == null || yearInt !in 1950..2100) {
            releaseYearError = "El año debe estar entre 1950 y 2100"
            hasError = true
        }

        if (platforms.isEmpty()) {
            platformsError = "Selecciona al menos una plataforma"
            hasError = true
        }

        if (isOwned) {
            if (obtainedDate == null) {
                datesError = "Indica la fecha de obtención"
                hasError = true
            } else if (obtainedDate.isAfter(today)) {
                datesError = "La fecha de obtención no puede ser futura"
                hasError = true
            }
        }

        if (isCompleted) {
            if (!isOwned) {
                datesError = "No puedes haber completado un juego que no posees"
                hasError = true
            } else if (completedDate == null) {
                datesError = "Indica la fecha de finalización"
                hasError = true
            } else if (completedDate.isAfter(today)) {
                datesError = "La fecha de finalización no puede ser futura"
                hasError = true
            } else if (obtainedDate != null && completedDate.isBefore(obtainedDate)) {
                datesError = "La fecha de finalización debe ser posterior a la fecha de obtención"
                hasError = true
            }
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    formTitleError = titleError,
                    formDeveloperError = developerError,
                    formGenreError = genreError,
                    formReleaseYearError = releaseYearError,
                    formPlatformsError = platformsError,
                    formDatesError = datesError
                )
            }
            return
        }

        val draft = VideojuegoDraft(
            title = title,
            description = state.formDescription.trim().ifBlank { null },
            platforms = platforms,
            developer = developer,
            genre = genre,
            releaseYear = yearInt!!,
            isOwned = isOwned,
            obtainedDate = if (isOwned) obtainedDate else null,
            isCompleted = if (isOwned) isCompleted else false,
            completedDate = if (isOwned && isCompleted) completedDate else null,
            rating = rating
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isFormSubmitting = true,
                    error = null
                )
            }

            try {
                val formId = state.formId
                if (formId == null) {
                    val created = gameRepository.createGame(draft)
                    _uiState.update {
                        it.copy(
                            games = listOf(created) + it.games,
                            selectedGame = created,
                            isFormSubmitting = false,
                            userMessage = "¡Videojuego agregado exitosamente!"
                        )
                    }
                } else {
                    val updated = gameRepository.updateGame(formId, draft)
                    _uiState.update {
                        it.copy(
                            games = it.games.map { g -> if (g.id == formId) updated else g },
                            selectedGame = updated,
                            isFormSubmitting = false,
                            userMessage = "¡Videojuego actualizado exitosamente!"
                        )
                    }
                }
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isFormSubmitting = false,
                        error = e.message ?: "Error al guardar el videojuego"
                    )
                }
            }
        }
    }

    // --- Eliminar Videojuego (con confirmación en UI, RF-09) ---
    fun deleteGame(id: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                gameRepository.deleteGame(id)
                _uiState.update {
                    it.copy(
                        games = it.games.filter { g -> g.id != id },
                        selectedGame = if (it.selectedGame?.id == id) null else it.selectedGame,
                        isLoading = false,
                        userMessage = "Videojuego eliminado correctamente"
                    )
                }
                onSuccess()
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

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
