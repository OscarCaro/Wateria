@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.wateria.revamp.feature.onboarding

import com.wateria.domain.usecase.CompleteOnboardingUseCase
import com.wateria.domain.usecase.ObserveOnboardingVersionUseCase
import com.wateria.revamp.FakeSettingsRepository
import com.wateria.revamp.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OnboardingViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `fresh install remains visible until final completion`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel =
            OnboardingViewModel(
                ObserveOnboardingVersionUseCase(repository),
                CompleteOnboardingUseCase(repository)
            )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.next()
        viewModel.next()
        assertTrue(viewModel.uiState.value.isVisible)
        assertEquals(2, viewModel.uiState.value.page)

        viewModel.complete()
        advanceUntilIdle()

        assertEquals(1, repository.onboardingVersion.value)
        assertFalse(viewModel.uiState.value.isVisible)
    }
}
