package com.wateria.revamp

import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantId
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

class FakePlantRepository(initialPlants: List<Plant> = emptyList()) : PlantRepository {
    val plants = MutableStateFlow(initialPlants)

    override fun observePlants(): Flow<List<Plant>> = plants

    override suspend fun getPlant(id: PlantId): Plant? = plants.value.firstOrNull { it.id == id }

    override suspend fun insert(plant: Plant) {
        plants.value += plant
    }

    override suspend fun update(plant: Plant) {
        plants.value =
            plants.value.map { current -> if (current.id == plant.id) plant else current }
    }

    override suspend fun delete(id: PlantId) {
        plants.value = plants.value.filterNot { plant -> plant.id == id }
    }

    override suspend fun deleteAll() {
        plants.value = emptyList()
    }
}

class FakeReminderScheduler : ReminderScheduler {
    var schedules: Int = 0

    override suspend fun scheduleNextReminder() {
        schedules++
    }

    override suspend fun cancelDailyReminder() = Unit

    override suspend fun scheduleSnooze(duration: Duration) = Unit

    override suspend fun cancelSnooze() = Unit
}

class FakeTimeProvider(var date: LocalDate) : TimeProvider {
    override fun instant(): Instant = date.atStartOfDay().toInstant(ZoneOffset.UTC)

    override fun today(): LocalDate = date

    override fun zoneId(): ZoneId = ZoneOffset.UTC
}

class SequentialPlantIdGenerator : PlantIdGenerator {
    private var next = 1

    override fun nextId(): PlantId = testPlantId(next++)
}

fun testPlantId(suffix: Int): PlantId = PlantId.from(
    UUID.fromString("00000000-0000-0000-0000-${suffix.toString().padStart(12, '0')}")
)
