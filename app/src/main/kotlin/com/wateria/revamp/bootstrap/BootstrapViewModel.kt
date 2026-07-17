package com.wateria.revamp.bootstrap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wateria.data.migration.LegacyMigration
import com.wateria.data.migration.LegacyMigrationFailureReason
import com.wateria.data.migration.LegacyMigrationResult
import com.wateria.domain.repository.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BootstrapUiState {
    data object Loading : BootstrapUiState

    data object Ready : BootstrapUiState

    data class Recovery(val reason: LegacyMigrationFailureReason?) : BootstrapUiState
}

@HiltViewModel
class BootstrapViewModel
@Inject
constructor(
    private val legacyMigration: LegacyMigration,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow<BootstrapUiState>(BootstrapUiState.Loading)
    val uiState: StateFlow<BootstrapUiState> = _uiState.asStateFlow()

    private var migrationJob: Job? = null

    init {
        retry()
    }

    fun retry() {
        if (migrationJob?.isActive == true) return
        _uiState.value = BootstrapUiState.Loading
        migrationJob =
            viewModelScope.launch {
                _uiState.value =
                    try {
                        when (val result = legacyMigration.run()) {
                            is LegacyMigrationResult.Completed,
                            is LegacyMigrationResult.AlreadyComplete -> {
                                runCatching { reminderScheduler.scheduleNextReminder() }
                                BootstrapUiState.Ready
                            }

                            is LegacyMigrationResult.Failed ->
                                BootstrapUiState.Recovery(result.reason)
                        }
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (_: Exception) {
                        BootstrapUiState.Recovery(reason = null)
                    }
            }
    }
}
