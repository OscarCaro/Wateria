package com.wateria.domain.usecase

import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.PlantId
import com.wateria.domain.model.PlantOrder
import com.wateria.domain.model.WateringInterval
import com.wateria.domain.model.WateringStatus
import com.wateria.domain.model.wateringStatus
import com.wateria.domain.repository.PlantRepository
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.time.PlantIdGenerator
import com.wateria.domain.time.TimeProvider
import com.wateria.domain.validation.PlantNameValidator
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlantNotFoundException(id: PlantId) :
    IllegalArgumentException("No plant exists with id $id")

class ObservePlantsUseCase(private val repository: PlantRepository) {
    operator fun invoke(): Flow<List<Plant>> =
        repository.observePlants().map { plants -> plants.sortedWith(PlantOrder) }
}

class GetPlantUseCase(private val repository: PlantRepository) {
    suspend operator fun invoke(id: PlantId): Plant? = repository.getPlant(id)
}

class CreatePlantUseCase(
    private val repository: PlantRepository,
    private val idGenerator: PlantIdGenerator,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(
        name: String,
        icon: PlantIcon,
        wateringInterval: WateringInterval,
        nextWateringDate: LocalDate
    ): Plant {
        val plant =
            Plant(
                id = idGenerator.nextId(),
                name = PlantNameValidator.normalizeAndValidate(name),
                icon = icon,
                wateringInterval = wateringInterval,
                nextWateringDate = nextWateringDate
            )
        repository.insert(plant)
        reminderScheduler.scheduleNextReminder()
        return plant
    }
}

class UpdatePlantUseCase(
    private val repository: PlantRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(plant: Plant): Plant {
        if (repository.getPlant(plant.id) == null) throw PlantNotFoundException(plant.id)
        val normalized = plant.copy(name = PlantNameValidator.normalizeAndValidate(plant.name))
        repository.update(normalized)
        reminderScheduler.scheduleNextReminder()
        return normalized
    }
}

class WaterPlantUseCase(
    private val repository: PlantRepository,
    private val timeProvider: TimeProvider,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(id: PlantId): Plant {
        val plant = repository.getPlant(id) ?: throw PlantNotFoundException(id)
        val watered =
            plant.copy(
                nextWateringDate = timeProvider.today().plusDays(
                    plant.wateringInterval.days.toLong()
                )
            )
        repository.update(watered)
        reminderScheduler.scheduleNextReminder()
        return watered
    }
}

class DeletePlantUseCase(
    private val repository: PlantRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(id: PlantId) {
        repository.delete(id)
        reminderScheduler.scheduleNextReminder()
    }
}

class DeleteAllPlantsUseCase(
    private val repository: PlantRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke() {
        repository.deleteAll()
        reminderScheduler.cancelDailyReminder()
        reminderScheduler.cancelSnooze()
        reminderScheduler.cancelDisplayedReminder()
    }
}

data class DuePlant(val plant: Plant, val status: WateringStatus)

class GetDuePlantsUseCase(
    private val repository: PlantRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(): List<DuePlant> = repository
        .observePlants()
        .first()
        .map { plant -> DuePlant(plant, plant.wateringStatus(timeProvider.today())) }
        .filterNot { duePlant -> duePlant.status is WateringStatus.Upcoming }
        .sortedWith(
            compareBy<DuePlant> {
                it.plant.nextWateringDate
            }.thenBy { it.plant.id.value }
        )
}
