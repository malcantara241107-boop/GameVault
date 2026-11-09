package com.example.gamevault.presentation.games.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gamevault.domain.model.GameConstants
import com.example.gamevault.presentation.games.viewmodels.GameUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameFormScreen(
    state: GameUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDeveloperChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    onReleaseYearChange: (String) -> Unit,
    onTogglePlatform: (String) -> Unit,
    onIsOwnedChange: (Boolean) -> Unit,
    onObtainedDateChange: (LocalDate?) -> Unit,
    onIsCompletedChange: (Boolean) -> Unit,
    onCompletedDateChange: (LocalDate?) -> Unit,
    onRatingChange: (Float) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditMode = state.formId != null
    val screenTitle = if (isEditMode) "Editar Videojuego" else "Nuevo Videojuego"
    var genreDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSave,
                        enabled = !state.isFormSubmitting
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Guardar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            OutlinedTextField(
                value = state.formTitle,
                onValueChange = onTitleChange,
                label = { Text("Título del videojuego *") },
                isError = state.formTitleError != null,
                supportingText = {
                    state.formTitleError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Desarrollador
            OutlinedTextField(
                value = state.formDeveloper,
                onValueChange = onDeveloperChange,
                label = { Text("Desarrollador *") },
                isError = state.formDeveloperError != null,
                supportingText = {
                    state.formDeveloperError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Género (Selector con DropdownMenu)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.formGenre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Género *") },
                    trailingIcon = {
                        IconButton(onClick = { genreDropdownExpanded = !genreDropdownExpanded }) {
                            Icon(
                                imageVector = if (genreDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    },
                    isError = state.formGenreError != null,
                    supportingText = {
                        state.formGenreError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = genreDropdownExpanded,
                    onDismissRequest = { genreDropdownExpanded = false }
                ) {
                    GameConstants.GENRES.forEach { genreOption ->
                        DropdownMenuItem(
                            text = { Text(genreOption) },
                            onClick = {
                                onGenreChange(genreOption)
                                genreDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Año de lanzamiento
            OutlinedTextField(
                value = state.formReleaseYear,
                onValueChange = onReleaseYearChange,
                label = { Text("Año de lanzamiento (1950 - 2100) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.formReleaseYearError != null,
                supportingText = {
                    state.formReleaseYearError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Plataformas
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Plataformas *",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameConstants.PLATFORMS.forEach { platform ->
                        val isSelected = platform in state.formPlatforms
                        FilterChip(
                            selected = isSelected,
                            onClick = { onTogglePlatform(platform) },
                            label = { Text(platform) }
                        )
                    }
                }

                if (state.formPlatformsError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.formPlatformsError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Descripción
            OutlinedTextField(
                value = state.formDescription,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción (opcional)") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // Estado de Biblioteca: Poseído
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¿Está en tu biblioteca personal?",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Actívalo si posees una copia de este juego",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.formIsOwned,
                    onCheckedChange = onIsOwnedChange
                )
            }

            if (state.formIsOwned) {
                DatePickerField(
                    label = "Fecha de obtención",
                    selectedDate = state.formObtainedDate,
                    onDateSelected = onObtainedDateChange
                )
            }

            // Estado de Completado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¿Has completado este juego?",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Solo disponible si posees el juego",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.formIsCompleted,
                    onCheckedChange = onIsCompletedChange,
                    enabled = state.formIsOwned
                )
            }

            if (state.formIsCompleted && state.formIsOwned) {
                DatePickerField(
                    label = "Fecha de finalización",
                    selectedDate = state.formCompletedDate,
                    onDateSelected = onCompletedDateChange
                )
            }

            if (state.formDatesError != null) {
                Text(
                    text = state.formDatesError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            HorizontalDivider()

            // Calificación (Slider 0 a 10)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Calificación personal",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${String.format("%.1f", state.formRating)} / 10",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = state.formRating,
                    onValueChange = onRatingChange,
                    valueRange = 0f..10f,
                    steps = 19
                )
            }

            // Error general
            if (state.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Guardar
            Button(
                onClick = onSave,
                enabled = !state.isFormSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isFormSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditMode) "Actualizar videojuego" else "Guardar videojuego")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedCard(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = label, style = MaterialTheme.typography.labelMedium)
                Text(
                    text = selectedDate?.toString() ?: "Toca para seleccionar fecha",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showDialog) {
        val initialEpoch = selectedDate
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()

        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialEpoch)

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            onDateSelected(date)
                        }
                        showDialog = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
