package com.wateria.revamp.design

import com.wateria.domain.model.PlantIcon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantIconResourcesTest {
    @Test
    fun `every domain icon has exactly one presentation resource`() {
        val mappedKeys = plantIconOptions.map { option -> option.icon.key }

        assertEquals(44, mappedKeys.size)
        assertEquals(44, mappedKeys.toSet().size)
        assertEquals(PlantIcon.knownKeys, mappedKeys.toSet())
        assertTrue(plantIconOptions.all { option -> option.drawableRes != 0 })
    }
}
