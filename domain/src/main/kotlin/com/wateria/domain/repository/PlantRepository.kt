package com.wateria.domain.repository

import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantId
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>

    suspend fun getPlant(id: PlantId): Plant?

    suspend fun insert(plant: Plant)

    suspend fun update(plant: Plant)

    suspend fun delete(id: PlantId)

    suspend fun deleteAll()
}
