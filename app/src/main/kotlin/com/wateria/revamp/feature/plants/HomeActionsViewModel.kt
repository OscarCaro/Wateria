package com.wateria.revamp.feature.plants

import androidx.lifecycle.ViewModel
import com.wateria.external.ExternalLaunchResult
import com.wateria.external.PlantIdentificationLauncher
import com.wateria.external.StoreLauncher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

data class HomeActionsUiState(val showLensInstallDialog: Boolean = false)

sealed interface HomeActionsEffect {
    data object LaunchFailed : HomeActionsEffect
}

@HiltViewModel
class HomeActionsViewModel
@Inject
constructor(
    private val plantIdentificationLauncher: PlantIdentificationLauncher,
    private val storeLauncher: StoreLauncher
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeActionsUiState())
    private val effectsChannel = Channel<HomeActionsEffect>(Channel.BUFFERED)
    val uiState: StateFlow<HomeActionsUiState> = _uiState.asStateFlow()
    val effects: Flow<HomeActionsEffect> = effectsChannel.receiveAsFlow()

    fun identifyPlant() {
        when (plantIdentificationLauncher.launch()) {
            ExternalLaunchResult.LAUNCHED -> Unit

            ExternalLaunchResult.UNAVAILABLE ->
                _uiState.value = HomeActionsUiState(showLensInstallDialog = true)

            ExternalLaunchResult.FAILED -> effectsChannel.trySend(HomeActionsEffect.LaunchFailed)
        }
    }

    fun dismissLensDialog() {
        _uiState.value = HomeActionsUiState()
    }

    fun installLens() {
        _uiState.value = HomeActionsUiState()
        if (storeLauncher.open(GOOGLE_LENS_PACKAGE) != ExternalLaunchResult.LAUNCHED) {
            effectsChannel.trySend(HomeActionsEffect.LaunchFailed)
        }
    }

    fun rateWateria() {
        if (storeLauncher.open(WATERIA_PACKAGE) != ExternalLaunchResult.LAUNCHED) {
            effectsChannel.trySend(HomeActionsEffect.LaunchFailed)
        }
    }

    private companion object {
        const val GOOGLE_LENS_PACKAGE = "com.google.ar.lens"
        const val WATERIA_PACKAGE = "com.wateria"
    }
}
