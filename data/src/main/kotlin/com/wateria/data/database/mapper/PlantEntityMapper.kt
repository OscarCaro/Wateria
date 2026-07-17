package com.wateria.data.database.mapper

import com.wateria.data.database.entity.PlantEntity
import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.PlantId
import com.wateria.domain.model.WateringInterval
import java.time.LocalDate

internal fun PlantEntity.toDomain(): Plant = Plant(
    id = PlantId.from(id),
    name = name,
    icon = PlantIcon.fromKey(iconKey),
    wateringInterval = WateringInterval.fromDays(wateringIntervalDays),
    nextWateringDate = LocalDate.ofEpochDay(nextWateringEpochDay)
)

internal fun Plant.toEntity(createdAtEpochMillis: Long, updatedAtEpochMillis: Long): PlantEntity =
    PlantEntity(
        id = id.value,
        name = name,
        iconKey = icon.key,
        wateringIntervalDays = wateringInterval.days,
        nextWateringEpochDay = nextWateringDate.toEpochDay(),
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis
    )
