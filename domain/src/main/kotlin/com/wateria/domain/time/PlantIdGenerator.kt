package com.wateria.domain.time

import com.wateria.domain.model.PlantId
import java.util.UUID

fun interface PlantIdGenerator {
    fun nextId(): PlantId
}

class RandomPlantIdGenerator : PlantIdGenerator {
    override fun nextId(): PlantId = PlantId.from(UUID.randomUUID())
}
