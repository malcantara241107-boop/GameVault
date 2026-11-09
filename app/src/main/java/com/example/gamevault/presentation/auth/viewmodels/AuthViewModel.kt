package com.example.gamevault.presentation.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.domain.model.UserSession
import com.example.gamevault.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isCheckingSession: Boolean = true,
    val isLoading: Boolean = false,
    val session: UserSession? = null,
    val error: String? = null,

    // Campos y errores de validación de Login (gestionados en el ViewModel)
    val loginEmail: String = "",
    val loginPassword: String = "",
    val loginEmailError: String? = null,
    val loginPasswordError: String? = null,

    // Campos y errores de validación de Registro (gestionados en el ViewModel)
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = "",
    val registerNameError: String? = null,
    val registerEmailError: String? = null,
    val registerPasswordError: String? = null,
    val registerConfirmPasswordError: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    init {
        checkCurrentSession()
    }

    private fun checkCurrentSession() {
        viewModelScope.launch {
            try {
                val session = authRepository.getCurrentSession()
                _uiState.update {
                    it.copy(
                        isCheckingSession = false,
                        session = session
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCheckingSession = false,
                        error = e.message ?: "No se pudo recuperar la sesión"
                    )
                }
            }
        }
    }

    // --- Manejo de campos de Login ---
    fun onLoginEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                loginEmail = email,
                loginEmailError = null,
                error = null
            )
        }
    }

    fun onLoginPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                loginPassword = password,
                loginPasswordError = null,
                error = null
            )
        }
    }

    fun login() {
        val currentState = _uiState.value
        val email = currentState.loginEmail.trim()
        val password = currentState.loginPassword

        // Validaciones en ViewModel (RF-01, RF-02 y Rúbrica Ítem 1 y 3)
        var hasError = false
        var emailError: String? = null
        var passwordError: String? = null

        if (email.isBlank()) {
            emailError = "El correo electrónico no puede estar vacío"
            hasError = true
        } else if (!emailRegex.matches(email)) {
            emailError = "El formato de correo no es válido"
            hasError = true
        }

        if (password.isBlank()) {
            passwordError = "La contraseña no puede estar vacía"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    loginEmailError = emailError,
                    loginPasswordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    loginEmailError = null,
                    loginPasswordError = null
                )
            }

            try {
                val session = authRepository.login(email, password)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        session = session,
                        loginPassword = "",
                        error = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al iniciar sesión"
                    )
                }
            }
        }
    }

    // --- Manejo de campos de Registro ---
    fun onRegisterNameChanged(name: String) {
        _uiState.update {
            it.copy(
                registerName = name,
                registerNameError = null,
                error = null
            )
        }
    }

    fun onRegisterEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                registerEmail = email,
                registerEmailError = null,
                error = null
            )
        }
    }

    fun onRegisterPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                registerPassword = password,
                registerPasswordError = null,
                error = null
            )
        }
    }

    fun onRegisterConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update {
            it.copy(
                registerConfirmPassword = confirmPassword,
                registerConfirmPasswordError = null,
                error = null
            )
        }
    }

    fun register() {
        val currentState = _uiState.value
        val name = currentState.registerName.trim()
        val email = currentState.registerEmail.trim()
        val password = currentState.registerPassword
        val confirmPassword = currentState.registerConfirmPassword

        // Validaciones en ViewModel requeridas por RF-01
        var hasError = false
        var nameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null

        if (name.isBlank()) {
            nameError = "El nombre es obligatorio"
            hasError = true
        }

        if (email.isBlank()) {
            emailError = "El correo electrónico es obligatorio"
            hasError = true
        } else if (!emailRegex.matches(email)) {
            emailError = "El formato de correo no es válido"
            hasError = true
        }

        if (password.isBlank()) {
            passwordError = "La contraseña es obligatoria"
            hasError = true
        } else if (password.length < 8) {
            passwordError = "La contraseña debe tener al menos 8 caracteres"
            hasError = true
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Por favor confirma tu contraseña"
            hasError = true
        } else if (password != confirmPassword) {
            confirmPasswordError = "Las contraseñas no coinciden"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    registerNameError = nameError,
                    registerEmailError = emailError,
                    registerPasswordError = passwordError,
                    registerConfirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    registerNameError = null,
                    registerEmailError = null,
                    registerPasswordError = null,
                    registerConfirmPasswordError = null
                )
            }

            try {
                val session = authRepository.register(name, email, password)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        session = session,
                        registerPassword = "",
                        registerConfirmPassword = "",
                        error = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al registrar la cuenta"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                authRepository.logout()
            } catch (_: Exception) {
                // Se asegura el cierre local de todas formas
            } finally {
                _uiState.update {
                    AuthUiState(
                        isCheckingSession = false,
                        isLoading = false,
                        session = null,
                        error = null
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
}
