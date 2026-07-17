@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.wateria.revamp.feature.plants

import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.WateringInterval
import com.wateria.domain.usecase.DeletePlantUseCase
import com.wateria.domain.usecase.ObservePlantsUseCase
import com.wateria.domain.usecase.WaterPlantUseCase
import com.wateria.revamp.FakePlantRepository
import com.wateria.revamp.FakeReminderScheduler
import com.wateria.revamp.FakeTimeProvider
import com.wateria.revamp.MainDispatcherRule
import com.wateria.revamp.testPlantId
import java.time.LocalDate
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

class PlantsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 7, 17)

    @Test
    fun `list exposes sorted upcoming due and overdue states`() = runTest {
        val repository =
            FakePlantRepository(
                listOf(
                    plant(1, "Upcoming", today.plusDays(3)),
                    plant(2, "Due", today),
                    plant(3, "Overdue", today.minusDays(2))
                )
            )
        val viewModel = viewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(listOf("Overdue", "Due", "Upcoming"), state.plants.map { it.name })
        assertEquals(WateringUiState.Overdue(2), state.plants[0].watering)
        assertEquals(WateringUiState.DueToday, state.plants[1].watering)
        assertEquals(WateringUiState.Upcoming(3), state.plants[2].watering)
    }

    @Test
    fun `watering updates the date and list position through the repository`() = runTest {
        val repository =
            FakePlantRepository(
                listOf(
                    plant(1, "Due", today, intervalDays = 7),
                    plant(2, "Soon", today.plusDays(2))
                )
            )
        val viewModel = viewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.water(testPlantId(1))
        advanceUntilIdle()

        assertEquals(today.plusDays(7), repository.getPlant(testPlantId(1))?.nextWateringDate)
        assertEquals(listOf("Soon", "Due"), viewModel.uiState.value.plants.map { it.name })
        assertTrue(viewModel.uiState.value.plants.none { it.isBusy })
    }

    @Test
    fun `delete removes a plant by stable id`() = runTest {
        val repository = FakePlantRepository(listOf(plant(1, "Fern", today)))
        val viewModel = viewModel(repository)

        viewModel.delete(testPlantId(1))
        advanceUntilIdle()

        assertTrue(repository.plants.value.isEmpty())
    }

    private fun viewModel(repository: FakePlantRepository): PlantsViewModel {
        val scheduler = FakeReminderScheduler()
        return PlantsViewModel(
            observePlants = ObservePlantsUseCase(repository),
            waterPlant = WaterPlantUseCase(repository, FakeTimeProvider(today), scheduler),
            deletePlant = DeletePlantUseCase(repository, scheduler),
            timeProvider = FakeTimeProvider(today)
        )
    }

    private fun plant(id: Int, name: String, nextDate: LocalDate, intervalDays: Int = 5): Plant =
        Plant(
            id = testPlantId(id),
            name = name,
            icon = PlantIcon.fromKey("common_01"),
            wateringInterval = WateringInterval.fromDays(intervalDays),
            nextWateringDate = nextDate
        )
}
