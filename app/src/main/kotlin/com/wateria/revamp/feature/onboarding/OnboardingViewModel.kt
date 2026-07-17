package com.wateria.revamp.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wateria.domain.usecase.CompleteOnboardingUseCase
import com.wateria.domain.usecase.ObserveOnboardingVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val isLoading: Boolean = true,
    val isVisible: Boolean = false,
    val page: Int = 0,
    val isCompleting: Boolean = false
)

sealed interface OnboardingEffect {
    data object Completed : OnboardingEffect
}

@HiltViewModel
class OnboardingViewModel
@Inject
constructor(
    observeOnboardingVersion: ObserveOnboardingVersionUseCase,
    private val completeOnboarding: CompleteOnboardingUseCase
) : ViewModel() {
    private val page = MutableStateFlow(0)
    private val isCompleting = MutableStateFlow(false)
    private val effectsChannel = Channel<OnboardingEffect>(Channel.BUFFERED)

    val effects: Flow<OnboardingEffect> = effectsChannel.receiveAsFlow()
    val uiState: StateFlow<OnboardingUiState> =
        combine(observeOnboardingVersion(), page, isCompleting) {
                version,
                currentPage,
                completing
            ->
            OnboardingUiState(
                isLoading = false,
                isVisible = version < CURRENT_ONBOARDING_VERSION,
                page = currentPage,
                isCompleting = completing
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = OnboardingUiState()
        )

    fun next() {
        page.value = (page.value + 1).coerceAtMost(LAST_PAGE)
    }

    fun complete() {
        if (isCompleting.value) return
        viewModelScope.launch {
            isCompleting.value = true
            try {
                completeOnboarding(CURRENT_ONBOARDING_VERSION)
                effectsChannel.send(OnboardingEffect.Completed)
            } finally {
                isCompleting.value = false
            }
        }
    }

    private companion object {
        const val CURRENT_ONBOARDING_VERSION = 1
        const val LAST_PAGE = 2
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
