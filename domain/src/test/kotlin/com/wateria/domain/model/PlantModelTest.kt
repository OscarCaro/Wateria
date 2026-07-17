package com.wateria.domain.model

import java.time.LocalDate
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantModelTest {
    @Test
    fun `watering status keeps upcoming due and overdue distinct`() {
        val today = LocalDate.of(2026, 7, 17)

        assertEquals(
            WateringStatus.Upcoming(3),
            plant(nextWateringDate = today.plusDays(3)).wateringStatus(today)
        )
        assertEquals(WateringStatus.DueToday, plant(nextWateringDate = today).wateringStatus(today))
        assertEquals(
            WateringStatus.Overdue(4),
            plant(nextWateringDate = today.minusDays(4)).wateringStatus(today)
        )
    }

    @Test
    fun `plant ordering uses date then case insensitive name then stable id`() {
        val date = LocalDate.of(2026, 7, 17)
        val later =
            plant(id = id(4), name = "Earlier alphabetically", nextWateringDate = date.plusDays(1))
        val beta = plant(id = id(3), name = "beta", nextWateringDate = date)
        val alphaSecond = plant(id = id(2), name = "Alpha", nextWateringDate = date)
        val alphaFirst = plant(id = id(1), name = "alpha", nextWateringDate = date)

        assertEquals(
            listOf(alphaFirst, alphaSecond, beta, later),
            listOf(later, beta, alphaSecond, alphaFirst).sortedWith(PlantOrder)
        )
    }

    @Test
    fun `icon catalog contains all parity keys plus explicit fallback`() {
        assertEquals(44, PlantIcon.knownKeys.size)
        assertTrue(PlantIcon.knownKeys.all { key -> PlantIcon.fromKey(key).key == key })
        assertEquals(PlantIcon.UnknownLegacy, PlantIcon.fromKey("future_or_corrupt"))
    }

    @Test
    fun `watering interval clamps legacy values but rejects invalid new values`() {
        assertEquals(1, WateringInterval.clamp(-10).days)
        assertEquals(40, WateringInterval.clamp(99).days)
        assertEquals(7, WateringInterval.fromDays(7).days)
        assertThrows(IllegalArgumentException::class.java) { WateringInterval.fromDays(0) }
        assertThrows(IllegalArgumentException::class.java) { WateringInterval.fromDays(41) }
    }

    private fun plant(
        id: PlantId = id(1),
        name: String = "Monstera",
        nextWateringDate: LocalDate
    ): Plant = Plant(
        id = id,
        name = name,
        icon = PlantIcon.fromKey("common_08_monstera"),
        wateringInterval = WateringInterval.fromDays(5),
        nextWateringDate = nextWateringDate
    )

    private fun id(suffix: Int): PlantId = PlantId.from(
        UUID.fromString("00000000-0000-0000-0000-${suffix.toString().padStart(12, '0')}")
    )
}
