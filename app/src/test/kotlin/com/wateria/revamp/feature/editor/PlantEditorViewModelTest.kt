@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.wateria.revamp.feature.editor

import androidx.lifecycle.SavedStateHandle
import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.WateringInterval
import com.wateria.domain.usecase.CreatePlantUseCase
import com.wateria.domain.usecase.DeletePlantUseCase
import com.wateria.domain.usecase.GetPlantUseCase
import com.wateria.domain.usecase.UpdatePlantUseCase
import com.wateria.revamp.FakePlantRepository
import com.wateria.revamp.FakeReminderScheduler
import com.wateria.revamp.FakeTimeProvider
import com.wateria.revamp.MainDispatcherRule
import com.wateria.revamp.SequentialPlantIdGenerator
import com.wateria.revamp.testPlantId
import java.time.LocalDate
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PlantEditorViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 7, 17)

    @Test
    fun `add flow saves normalized fields and independently selected next date`() = runTest {
        val repository = FakePlantRepository()
        val viewModel = viewModel(repository)

        viewModel.updateName("  Kitchen fern  ")
        viewModel.selectIcon(PlantIcon.fromKey("common_08_monstera"))
        viewModel.changeWateringInterval(2)
        viewModel.changeNextWatering(-3)
        viewModel.save()
        advanceUntilIdle()

        val plant = repository.plants.value.single()
        assertEquals("Kitchen fern", plant.name)
        assertEquals("common_08_monstera", plant.icon.key)
        assertEquals(7, plant.wateringInterval.days)
        assertEquals(today.plusDays(2), plant.nextWateringDate)
    }

    @Test
    fun `names over the approved limit stay in the editor`() = runTest {
        val repository = FakePlantRepository()
        val viewModel = viewModel(repository)

        viewModel.updateName("x".repeat(51))
        viewModel.save()

        assertEquals(PlantEditorError.NAME_TOO_LONG, viewModel.uiState.value.error)
        assertTrue(repository.plants.value.isEmpty())
    }

    @Test
    fun `edit flow loads by id and updates the same record`() = runTest {
        val original = plant(name = "Old", nextDate = today.minusDays(2))
        val repository = FakePlantRepository(listOf(original))
        val viewModel =
            viewModel(repository, SavedStateHandle(mapOf("plantId" to original.id.value)))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(0, viewModel.uiState.value.nextWateringDays)

        viewModel.updateName("New")
        viewModel.changeNextWatering(4)
        viewModel.save()
        advanceUntilIdle()

        val updated = repository.plants.value.single()
        assertEquals(original.id, updated.id)
        assertEquals("New", updated.name)
        assertEquals(today.plusDays(4), updated.nextWateringDate)
    }

    @Test
    fun `restored editor fields survive recreation and remain dirty`() = runTest {
        val original = plant(name = "Original", nextDate = today.plusDays(5))
        val repository = FakePlantRepository(listOf(original))
        val state =
            SavedStateHandle(
                mapOf(
                    "plantId" to original.id.value,
                    "editor_name" to "Restored draft",
                    "editor_icon" to "cactus_02",
                    "editor_interval" to 9,
                    "editor_next_watering" to 3
                )
            )

        val viewModel = viewModel(repository, state)
        advanceUntilIdle()

        assertEquals("Restored draft", viewModel.uiState.value.name)
        assertEquals("cactus_02", viewModel.uiState.value.selectedIcon.key)
        assertEquals(9, viewModel.uiState.value.wateringIntervalDays)
        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
    }

    @Test
    fun `confirmed delete action removes the current stable id`() = runTest {
        val original = plant(name = "Delete me", nextDate = today)
        val repository = FakePlantRepository(listOf(original))
        val viewModel =
            viewModel(repository, SavedStateHandle(mapOf("plantId" to original.id.value)))
        advanceUntilIdle()

        viewModel.delete()
        advanceUntilIdle()

        assertTrue(repository.plants.value.isEmpty())
    }

    private fun viewModel(
        repository: FakePlantRepository,
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ): PlantEditorViewModel {
        val scheduler = FakeReminderScheduler()
        return PlantEditorViewModel(
            savedStateHandle = savedStateHandle,
            getPlant = GetPlantUseCase(repository),
            createPlant =
                CreatePlantUseCase(repository, SequentialPlantIdGenerator(), scheduler),
            updatePlant = UpdatePlantUseCase(repository, scheduler),
            deletePlant = DeletePlantUseCase(repository, scheduler),
            timeProvider = FakeTimeProvider(today)
        )
    }

    private fun plant(name: String, nextDate: LocalDate): Plant = Plant(
        id = testPlantId(1),
        name = name,
        icon = PlantIcon.fromKey("common_01"),
        wateringInterval = WateringInterval.fromDays(5),
        nextWateringDate = nextDate
    )
}
