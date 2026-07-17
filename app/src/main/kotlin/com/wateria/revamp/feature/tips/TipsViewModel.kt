package com.wateria.revamp.feature.tips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wateria.domain.usecase.GetDailyTipUseCase
import com.wateria.domain.usecase.ObservePlantsUseCase
import com.wateria.domain.usecase.ShouldShowDailyTipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class TipUiState(
    val isLoading: Boolean = true,
    val index: Int = 0,
    val hoursUntilNext: Int = 0,
    val minutesUntilNext: Int = 0,
    val failed: Boolean = false
)

@HiltViewModel
class TipViewModel
@Inject
constructor(private val getDailyTip: GetDailyTipUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(TipUiState())
    val uiState: StateFlow<TipUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val tip = getDailyTip(TIP_COUNT)
                _uiState.value =
                    TipUiState(
                        isLoading = false,
                        index = tip.index,
                        hoursUntilNext = tip.hoursUntilNext,
                        minutesUntilNext = tip.minutesUntilNext
                    )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _uiState.value = TipUiState(isLoading = false, failed = true)
            }
        }
    }
}

sealed interface DailyTipPromptEffect {
    data object OpenTip : DailyTipPromptEffect
}

@HiltViewModel
class DailyTipPromptViewModel
@Inject
constructor(
    observePlants: ObservePlantsUseCase,
    private val shouldShowDailyTip: ShouldShowDailyTipUseCase,
    private val getDailyTip: GetDailyTipUseCase
) : ViewModel() {
    private val effectsChannel = Channel<DailyTipPromptEffect>(Channel.BUFFERED)
    val effects: Flow<DailyTipPromptEffect> = effectsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                observePlants().filter { plants -> plants.isNotEmpty() }.first()
                if (shouldShowDailyTip()) {
                    getDailyTip(TIP_COUNT)
                    effectsChannel.send(DailyTipPromptEffect.OpenTip)
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // A daily prompt is optional and must never disrupt the plant list.
            }
        }
    }
}

private const val TIP_COUNT = 7
