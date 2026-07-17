package com.wateria.domain.usecase

import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.PlantId
import com.wateria.domain.model.WateringInterval
import com.wateria.domain.model.WateringStatus
import com.wateria.domain.repository.PlantRepository
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.time.PlantIdGenerator
import com.wateria.domain.time.TimeProvider
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantUseCasesTest {
    private val today = LocalDate.of(2026, 7, 17)
    private val repository = FakePlantRepository()
    private val scheduler = FakeReminderScheduler()
    private val timeProvider = FixedTimeProvider(today)

    @Test
    fun `create normalizes user input and schedules reminders`() = runTest {
        val expectedId = id(1)
        val useCase = CreatePlantUseCase(repository, PlantIdGenerator { expectedId }, scheduler)

        val result =
            useCase(
                name = "  Monstera  ",
                icon = PlantIcon.fromKey("common_08_monstera"),
                wateringInterval = WateringInterval.fromDays(5),
                nextWateringDate = today.plusDays(2)
            )

        assertEquals("Monstera", result.name)
        assertEquals(result, repository.getPlant(expectedId))
        assertEquals(1, scheduler.dailySchedules)
    }

    @Test
    fun `watering uses injected today plus interval`() = runTest {
        val original = plant(id(1), today.minusDays(3), intervalDays = 7)
        repository.insert(original)

        val watered = WaterPlantUseCase(repository, timeProvider, scheduler)(original.id)

        assertEquals(today.plusDays(7), watered.nextWateringDate)
        assertEquals(watered, repository.getPlant(original.id))
    }

    @Test
    fun `due plants include today and overdue while excluding upcoming`() = runTest {
        repository.insert(plant(id(1), today.plusDays(1)))
        repository.insert(plant(id(2), today))
        repository.insert(plant(id(3), today.minusDays(2)))

        val due = GetDuePlantsUseCase(repository, timeProvider)()

        assertEquals(listOf(id(3), id(2)), due.map { it.plant.id })
        assertTrue(due[0].status is WateringStatus.Overdue)
        assertEquals(WateringStatus.DueToday, due[1].status)
    }

    @Test
    fun `delete all clears data and both reminder work streams`() = runTest {
        repository.insert(plant(id(1), today))

        DeleteAllPlantsUseCase(repository, scheduler)()

        assertTrue(repository.values.value.isEmpty())
        assertEquals(1, scheduler.dailyCancellations)
        assertEquals(1, scheduler.snoozeCancellations)
    }

    private fun plant(id: PlantId, date: LocalDate, intervalDays: Int = 5): Plant = Plant(
        id = id,
        name = "Plant ${id.value.takeLast(1)}",
        icon = PlantIcon.fromKey("cactus_01"),
        wateringInterval = WateringInterval.fromDays(intervalDays),
        nextWateringDate = date
    )

    private fun id(suffix: Int): PlantId = PlantId.from(
        UUID.fromString("00000000-0000-0000-0000-${suffix.toString().padStart(12, '0')}")
    )
}

private class FakePlantRepository : PlantRepository {
    val values = MutableStateFlow<List<Plant>>(emptyList())

    override fun observePlants(): Flow<List<Plant>> = values

    override suspend fun getPlant(id: PlantId): Plant? = values.value.firstOrNull { it.id == id }

    override suspend fun insert(plant: Plant) {
        values.value = values.value + plant
    }

    override suspend fun update(plant: Plant) {
        values.value =
            values.value.map { existing -> if (existing.id == plant.id) plant else existing }
    }

    override suspend fun delete(id: PlantId) {
        values.value = values.value.filterNot { it.id == id }
    }

    override suspend fun deleteAll() {
        values.value = emptyList()
    }
}

private class FakeReminderScheduler : ReminderScheduler {
    var dailySchedules = 0
    var dailyCancellations = 0
    var snoozeCancellations = 0

    override suspend fun scheduleNextReminder() {
        dailySchedules++
    }

    override suspend fun cancelDailyReminder() {
        dailyCancellations++
    }

    override suspend fun scheduleSnooze(duration: Duration) = Unit

    override suspend fun cancelSnooze() {
        snoozeCancellations++
    }
}

private class FixedTimeProvider(private val date: LocalDate) : TimeProvider {
    override fun instant(): Instant = date.atStartOfDay().toInstant(ZoneOffset.UTC)

    override fun today(): LocalDate = date

    override fun zoneId(): ZoneId = ZoneOffset.UTC
}
