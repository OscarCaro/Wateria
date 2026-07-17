package com.wateria.revamp.feature.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.PlantId
import com.wateria.domain.model.WateringInterval
import com.wateria.domain.time.TimeProvider
import com.wateria.domain.usecase.CreatePlantUseCase
import com.wateria.domain.usecase.DeletePlantUseCase
import com.wateria.domain.usecase.GetPlantUseCase
import com.wateria.domain.usecase.UpdatePlantUseCase
import com.wateria.domain.validation.InvalidPlantNameException
import com.wateria.domain.validation.PlantNameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val DEFAULT_ICON_KEY = "common_01"
private const val DEFAULT_INTERVAL_DAYS = 5
private const val MIN_NEXT_WATERING_DAYS = 0
private const val MAX_NEXT_WATERING_DAYS = 40

data class PlantEditorUiState(
    val isEditing: Boolean,
    val isLoading: Boolean,
    val isSaving: Boolean = false,
    val name: String = "",
    val selectedIcon: PlantIcon = PlantIcon.fromKey(DEFAULT_ICON_KEY),
    val wateringIntervalDays: Int = DEFAULT_INTERVAL_DAYS,
    val nextWateringDays: Int = DEFAULT_INTERVAL_DAYS,
    val error: PlantEditorError? = null,
    val hasUnsavedChanges: Boolean = false
)

enum class PlantEditorError {
    NAME_REQUIRED,
    NAME_TOO_LONG,
    PLANT_NOT_FOUND,
    SAVE_FAILED,
    DELETE_FAILED
}

sealed interface PlantEditorEffect {
    data object Saved : PlantEditorEffect

    data object Deleted : PlantEditorEffect
}

@HiltViewModel
@Suppress("TooManyFunctions")
class PlantEditorViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getPlant: GetPlantUseCase,
    private val createPlant: CreatePlantUseCase,
    private val updatePlant: UpdatePlantUseCase,
    private val deletePlant: DeletePlantUseCase,
    private val timeProvider: TimeProvider
) : ViewModel() {
    private val plantId = savedStateHandle.get<String>(PLANT_ID_KEY)?.let(PlantId::parseOrNull)
    private val hasRestoredFields = savedStateHandle.contains(NAME_KEY)
    private val defaultValues = EditorValues()
    private var baselineValues = defaultValues
    private var loadedPlant: Plant? = null
    private val effectChannel = Channel<PlantEditorEffect>(Channel.BUFFERED)

    private val _uiState =
        MutableStateFlow(
            PlantEditorUiState(
                isEditing = savedStateHandle.get<String>(PLANT_ID_KEY) != null,
                isLoading = savedStateHandle.get<String>(PLANT_ID_KEY) != null,
                name = savedStateHandle[NAME_KEY] ?: defaultValues.name,
                selectedIcon =
                    PlantIcon.fromKey(savedStateHandle[ICON_KEY] ?: defaultValues.icon.key),
                wateringIntervalDays =
                    savedStateHandle[INTERVAL_KEY] ?: defaultValues.wateringIntervalDays,
                nextWateringDays =
                    savedStateHandle[NEXT_WATERING_KEY] ?: defaultValues.nextWateringDays
            )
        )
    val uiState: StateFlow<PlantEditorUiState> = _uiState.asStateFlow()
    val effects: Flow<PlantEditorEffect> = effectChannel.receiveAsFlow()

    init {
        if (_uiState.value.isEditing) {
            loadPlant()
        }
    }

    fun updateName(name: String) {
        updateFields(savedStateHandle) { state -> state.copy(name = name, error = null) }
    }

    fun selectIcon(icon: PlantIcon) {
        updateFields(savedStateHandle) { state -> state.copy(selectedIcon = icon, error = null) }
    }

    fun changeWateringInterval(delta: Int) {
        updateFields(savedStateHandle) { state ->
            state.copy(
                wateringIntervalDays =
                    (state.wateringIntervalDays + delta).coerceIn(
                        WateringInterval.MIN_DAYS,
                        WateringInterval.MAX_DAYS
                    ),
                error = null
            )
        }
    }

    fun changeNextWatering(delta: Int) {
        updateFields(savedStateHandle) { state ->
            state.copy(
                nextWateringDays =
                    (state.nextWateringDays + delta).coerceIn(
                        MIN_NEXT_WATERING_DAYS,
                        MAX_NEXT_WATERING_DAYS
                    ),
                error = null
            )
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.isLoading || state.isSaving) return
        val normalizedName = validateName(state.name) ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                val current = _uiState.value
                val interval = WateringInterval.fromDays(current.wateringIntervalDays)
                val nextWateringDate =
                    timeProvider.today().plusDays(current.nextWateringDays.toLong())
                val original = loadedPlant
                if (current.isEditing) {
                    requireNotNull(original)
                    updatePlant(
                        original.copy(
                            name = normalizedName,
                            icon = current.selectedIcon,
                            wateringInterval = interval,
                            nextWateringDate = nextWateringDate
                        )
                    )
                } else {
                    createPlant(normalizedName, current.selectedIcon, interval, nextWateringDate)
                }
                effectChannel.send(PlantEditorEffect.Saved)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(isSaving = false, error = PlantEditorError.SAVE_FAILED)
            }
        }
    }

    private fun validateName(name: String): String? = try {
        PlantNameValidator.normalizeAndValidate(name)
    } catch (_: InvalidPlantNameException.Blank) {
        _uiState.value = _uiState.value.copy(error = PlantEditorError.NAME_REQUIRED)
        null
    } catch (_: InvalidPlantNameException.TooLong) {
        _uiState.value = _uiState.value.copy(error = PlantEditorError.NAME_TOO_LONG)
        null
    }

    fun delete() {
        val id = plantId ?: return
        val state = _uiState.value
        if (state.isLoading || state.isSaving) return
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            try {
                deletePlant(id)
                effectChannel.send(PlantEditorEffect.Deleted)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(isSaving = false, error = PlantEditorError.DELETE_FAILED)
            }
        }
    }

    private fun loadPlant() {
        val id = plantId
        if (id == null) {
            _uiState.value =
                _uiState.value.copy(isLoading = false, error = PlantEditorError.PLANT_NOT_FOUND)
            return
        }
        viewModelScope.launch {
            val plant = getPlant(id)
            if (plant == null) {
                _uiState.value =
                    _uiState.value.copy(isLoading = false, error = PlantEditorError.PLANT_NOT_FOUND)
                return@launch
            }
            loadedPlant = plant
            val nextDays =
                ChronoUnit.DAYS.between(timeProvider.today(), plant.nextWateringDate)
                    .toInt()
                    .coerceIn(MIN_NEXT_WATERING_DAYS, MAX_NEXT_WATERING_DAYS)
            baselineValues =
                EditorValues(
                    name = plant.name,
                    icon = plant.icon,
                    wateringIntervalDays = plant.wateringInterval.days,
                    nextWateringDays = nextDays
                )
            val fields = if (hasRestoredFields) _uiState.value.toValues() else baselineValues
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    name = fields.name,
                    selectedIcon = fields.icon,
                    wateringIntervalDays = fields.wateringIntervalDays,
                    nextWateringDays = fields.nextWateringDays,
                    hasUnsavedChanges = fields != baselineValues
                )
            persistFields(savedStateHandle, _uiState.value)
        }
    }

    private fun updateFields(
        savedStateHandle: SavedStateHandle,
        transform: (PlantEditorUiState) -> PlantEditorUiState
    ) {
        val updated = transform(_uiState.value)
        _uiState.value = updated.copy(hasUnsavedChanges = updated.toValues() != baselineValues)
        persistFields(savedStateHandle, _uiState.value)
    }

    private fun persistFields(savedStateHandle: SavedStateHandle, state: PlantEditorUiState) {
        savedStateHandle[NAME_KEY] = state.name
        savedStateHandle[ICON_KEY] = state.selectedIcon.key
        savedStateHandle[INTERVAL_KEY] = state.wateringIntervalDays
        savedStateHandle[NEXT_WATERING_KEY] = state.nextWateringDays
    }

    private fun PlantEditorUiState.toValues(): EditorValues =
        EditorValues(name, selectedIcon, wateringIntervalDays, nextWateringDays)

    private data class EditorValues(
        val name: String = "",
        val icon: PlantIcon = PlantIcon.fromKey(DEFAULT_ICON_KEY),
        val wateringIntervalDays: Int = DEFAULT_INTERVAL_DAYS,
        val nextWateringDays: Int = DEFAULT_INTERVAL_DAYS
    )

    private companion object {
        const val PLANT_ID_KEY = "plantId"
        const val NAME_KEY = "editor_name"
        const val ICON_KEY = "editor_icon"
        const val INTERVAL_KEY = "editor_interval"
        const val NEXT_WATERING_KEY = "editor_next_watering"
    }
}
