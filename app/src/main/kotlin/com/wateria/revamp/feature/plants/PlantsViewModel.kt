package com.wateria.revamp.feature.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.PlantId
import com.wateria.domain.model.WateringStatus
import com.wateria.domain.model.wateringStatus
import com.wateria.domain.time.TimeProvider
import com.wateria.domain.usecase.DeletePlantUseCase
import com.wateria.domain.usecase.ObservePlantsUseCase
import com.wateria.domain.usecase.WaterPlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
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

data class PlantsUiState(
    val isLoading: Boolean = true,
    val plants: List<PlantCardUiState> = emptyList()
)

data class PlantCardUiState(
    val id: PlantId,
    val name: String,
    val icon: PlantIcon,
    val wateringIntervalDays: Int,
    val watering: WateringUiState,
    val isBusy: Boolean
)

sealed interface WateringUiState {
    data class Upcoming(val daysRemaining: Int) : WateringUiState

    data object DueToday : WateringUiState

    data class Overdue(val daysOverdue: Int) : WateringUiState
}

sealed interface PlantsEffect {
    data object ActionFailed : PlantsEffect
}

@HiltViewModel
class PlantsViewModel
@Inject
constructor(
    observePlants: ObservePlantsUseCase,
    private val waterPlant: WaterPlantUseCase,
    private val deletePlant: DeletePlantUseCase,
    private val timeProvider: TimeProvider
) : ViewModel() {
    private val today = MutableStateFlow(timeProvider.today())
    private val busyPlantIds = MutableStateFlow<Set<PlantId>>(emptySet())
    private val effectChannel = Channel<PlantsEffect>(Channel.BUFFERED)

    val effects: Flow<PlantsEffect> = effectChannel.receiveAsFlow()

    val uiState: StateFlow<PlantsUiState> =
        combine(observePlants(), today, busyPlantIds) { plants, currentDate, busyIds ->
            PlantsUiState(
                isLoading = false,
                plants = plants.map { plant -> plant.toUiState(currentDate, plant.id in busyIds) }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = PlantsUiState()
        )

    fun refreshDate() {
        today.value = timeProvider.today()
    }

    fun water(id: PlantId) {
        perform(id) { waterPlant(id) }
    }

    fun delete(id: PlantId) {
        perform(id) { deletePlant(id) }
    }

    private fun perform(id: PlantId, action: suspend () -> Unit) {
        if (id in busyPlantIds.value) return
        viewModelScope.launch {
            busyPlantIds.value += id
            try {
                action()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                effectChannel.send(PlantsEffect.ActionFailed)
            } finally {
                busyPlantIds.value -= id
            }
        }
    }

    private fun Plant.toUiState(currentDate: LocalDate, isBusy: Boolean): PlantCardUiState =
        PlantCardUiState(
            id = id,
            name = name,
            icon = icon,
            wateringIntervalDays = wateringInterval.days,
            watering =
                when (val status = wateringStatus(currentDate)) {
                    is WateringStatus.Upcoming -> WateringUiState.Upcoming(status.daysRemaining)
                    WateringStatus.DueToday -> WateringUiState.DueToday
                    is WateringStatus.Overdue -> WateringUiState.Overdue(status.daysOverdue)
                },
            isBusy = isBusy
        )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
