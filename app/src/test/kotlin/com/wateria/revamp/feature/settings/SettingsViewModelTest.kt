@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.wateria.revamp.feature.settings

import com.wateria.domain.usecase.DeleteAllPlantsUseCase
import com.wateria.domain.usecase.ObserveReminderSettingsUseCase
import com.wateria.domain.usecase.UpdateReminderSettingsUseCase
import com.wateria.revamp.FakePlantRepository
import com.wateria.revamp.FakeReminderScheduler
import com.wateria.revamp.FakeSettingsRepository
import com.wateria.revamp.MainDispatcherRule
import java.time.LocalTime
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

class SettingsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `settings changes persist and orchestrate reminder cancellation`() = runTest {
        val settingsRepository = FakeSettingsRepository()
        val scheduler = FakeReminderScheduler()
        val viewModel = viewModel(settingsRepository, scheduler)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.setReminderTime(LocalTime.of(9, 30))
        advanceUntilIdle()
        viewModel.setSnoozeHours(4)
        advanceUntilIdle()
        viewModel.setRemindersEnabled(false)
        advanceUntilIdle()

        val stored = settingsRepository.reminders.value
        assertFalse(stored.isEnabled)
        assertEquals(LocalTime.of(9, 30), stored.time)
        assertEquals(4, stored.snoozeDuration.toHours())
        assertEquals(1, scheduler.dailyCancellations)
        assertEquals(1, scheduler.snoozeCancellations)
        assertEquals(1, scheduler.displayedCancellations)
    }

    @Test
    fun `delete all clears reactive plant storage`() = runTest {
        val plantRepository = FakePlantRepository()
        val scheduler = FakeReminderScheduler()
        val viewModel =
            SettingsViewModel(
                ObserveReminderSettingsUseCase(FakeSettingsRepository()),
                UpdateReminderSettingsUseCase(FakeSettingsRepository(), scheduler),
                DeleteAllPlantsUseCase(plantRepository, scheduler)
            )

        viewModel.deleteAll()
        advanceUntilIdle()

        assertTrue(plantRepository.plants.value.isEmpty())
        assertEquals(1, scheduler.dailyCancellations)
        assertEquals(1, scheduler.snoozeCancellations)
        assertEquals(1, scheduler.displayedCancellations)
    }

    private fun viewModel(
        repository: FakeSettingsRepository,
        scheduler: FakeReminderScheduler
    ): SettingsViewModel = SettingsViewModel(
        ObserveReminderSettingsUseCase(repository),
        UpdateReminderSettingsUseCase(repository, scheduler),
        DeleteAllPlantsUseCase(FakePlantRepository(), scheduler)
    )
}
