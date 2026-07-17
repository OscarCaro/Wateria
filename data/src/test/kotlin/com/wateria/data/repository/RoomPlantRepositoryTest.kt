package com.wateria.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.wateria.data.database.WateriaDatabase
import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.PlantId
import com.wateria.domain.model.WateringInterval
import com.wateria.domain.time.TimeProvider
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomPlantRepositoryTest {
    private lateinit var database: WateriaDatabase
    private lateinit var timeProvider: RepositoryTimeProvider
    private lateinit var repository: RoomPlantRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(context, WateriaDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        timeProvider = RepositoryTimeProvider()
        repository = RoomPlantRepository(database.plantDao(), timeProvider)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `repository exposes sorted reactive domain data and preserves creation timestamp`() =
        runTest {
            val later = plant(id(1), "Aloe", LocalDate.of(2026, 7, 21))
            val sooner = plant(id(2), "Monstera", LocalDate.of(2026, 7, 20))
            repository.insert(later)
            repository.insert(sooner)

            assertEquals(listOf(sooner, later), repository.observePlants().first())
            val createdAt = database.plantDao().getById(sooner.id.value)!!.createdAtEpochMillis

            timeProvider.currentInstant = timeProvider.currentInstant.plusSeconds(60)
            val renamed = sooner.copy(name = "Living room Monstera")
            repository.update(renamed)
            val updatedEntity = database.plantDao().getById(sooner.id.value)!!

            assertEquals(createdAt, updatedEntity.createdAtEpochMillis)
            assertEquals(
                timeProvider.currentInstant.toEpochMilli(),
                updatedEntity.updatedAtEpochMillis
            )
            assertEquals(renamed, repository.getPlant(sooner.id))
        }

    private fun plant(id: PlantId, name: String, date: LocalDate): Plant = Plant(
        id = id,
        name = name,
        icon = PlantIcon.fromKey("common_08_monstera"),
        wateringInterval = WateringInterval.fromDays(5),
        nextWateringDate = date
    )

    private fun id(suffix: Int): PlantId = PlantId.from(
        UUID.fromString("00000000-0000-0000-0000-${suffix.toString().padStart(12, '0')}")
    )
}

private class RepositoryTimeProvider : TimeProvider {
    var currentInstant: Instant = Instant.parse("2026-07-17T10:00:00Z")

    override fun instant(): Instant = currentInstant

    override fun today(): LocalDate = LocalDate.ofInstant(currentInstant, ZoneOffset.UTC)

    override fun zoneId(): ZoneId = ZoneOffset.UTC
}
