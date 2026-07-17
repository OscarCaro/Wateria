package com.wateria.data.repository

import com.wateria.data.database.dao.PlantDao
import com.wateria.data.database.mapper.toDomain
import com.wateria.data.database.mapper.toEntity
import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantId
import com.wateria.domain.model.PlantOrder
import com.wateria.domain.repository.PlantRepository
import com.wateria.domain.time.TimeProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomPlantRepository
@Inject
constructor(
    private val plantDao: PlantDao,
    private val timeProvider: TimeProvider
) : PlantRepository {
    override fun observePlants(): Flow<List<Plant>> = plantDao.observeAll().map { entities ->
        entities.map { entity -> entity.toDomain() }.sortedWith(PlantOrder)
    }

    override suspend fun getPlant(id: PlantId): Plant? = plantDao.getById(id.value)?.toDomain()

    override suspend fun insert(plant: Plant) {
        val now = timeProvider.instant().toEpochMilli()
        plantDao.insert(plant.toEntity(createdAtEpochMillis = now, updatedAtEpochMillis = now))
    }

    override suspend fun update(plant: Plant) {
        val existing = requireNotNull(plantDao.getById(plant.id.value)) {
            "Cannot update a plant that does not exist"
        }
        plantDao.update(
            plant.toEntity(
                createdAtEpochMillis = existing.createdAtEpochMillis,
                updatedAtEpochMillis = timeProvider.instant().toEpochMilli()
            )
        )
    }

    override suspend fun delete(id: PlantId) {
        plantDao.deleteById(id.value)
    }

    override suspend fun deleteAll() {
        plantDao.deleteAll()
    }
}
