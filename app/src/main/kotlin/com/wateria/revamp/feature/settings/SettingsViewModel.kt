package com.wateria.revamp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.usecase.DeleteAllPlantsUseCase
import com.wateria.domain.usecase.ObserveReminderSettingsUseCase
import com.wateria.domain.usecase.UpdateReminderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val reminderSettings: ReminderSettings = ReminderSettings()
)

sealed interface SettingsEffect {
    data object UpdateFailed : SettingsEffect

    data object PlantsDeleted : SettingsEffect
}

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    observeReminderSettings: ObserveReminderSettingsUseCase,
    private val updateReminderSettings: UpdateReminderSettingsUseCase,
    private val deleteAllPlants: DeleteAllPlantsUseCase
) : ViewModel() {
    private val isSaving = MutableStateFlow(false)
    private val effectsChannel = Channel<SettingsEffect>(Channel.BUFFERED)

    val effects: Flow<SettingsEffect> = effectsChannel.receiveAsFlow()
    val uiState: StateFlow<SettingsUiState> =
        combine(observeReminderSettings(), isSaving) { settings, saving ->
            SettingsUiState(
                isLoading = false,
                isSaving = saving,
                reminderSettings = settings
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = SettingsUiState()
        )

    fun setRemindersEnabled(enabled: Boolean) {
        update { settings -> settings.copy(isEnabled = enabled) }
    }

    fun setReminderTime(time: LocalTime) {
        update { settings -> settings.copy(time = time) }
    }

    fun setSnoozeHours(hours: Int) {
        update { settings ->
            settings.copy(
                snoozeDuration = Duration.ofHours(hours.coerceIn(MIN_HOURS, MAX_HOURS).toLong())
            )
        }
    }

    fun deleteAll() {
        if (isSaving.value) return
        viewModelScope.launch {
            isSaving.value = true
            try {
                deleteAllPlants()
                effectsChannel.send(SettingsEffect.PlantsDeleted)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                effectsChannel.send(SettingsEffect.UpdateFailed)
            } finally {
                isSaving.value = false
            }
        }
    }

    private fun update(transform: (ReminderSettings) -> ReminderSettings) {
        if (isSaving.value || uiState.value.isLoading) return
        val updated = transform(uiState.value.reminderSettings)
        viewModelScope.launch {
            isSaving.value = true
            try {
                updateReminderSettings(updated)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                effectsChannel.send(SettingsEffect.UpdateFailed)
            } finally {
                isSaving.value = false
            }
        }
    }

    private companion object {
        const val MIN_HOURS = 1
        const val MAX_HOURS = 23
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
