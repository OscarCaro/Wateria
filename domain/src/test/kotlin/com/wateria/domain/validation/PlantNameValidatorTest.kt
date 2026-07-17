package com.wateria.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PlantNameValidatorTest {
    @Test
    fun `name is trimmed and fifty characters are accepted`() {
        val name = "x".repeat(PlantNameValidator.MAX_LENGTH)

        assertEquals(name, PlantNameValidator.normalizeAndValidate("  $name  "))
    }

    @Test
    fun `blank and overlong names have distinct validation failures`() {
        assertThrows(InvalidPlantNameException.Blank::class.java) {
            PlantNameValidator.normalizeAndValidate("   ")
        }
        assertThrows(InvalidPlantNameException.TooLong::class.java) {
            PlantNameValidator.normalizeAndValidate("x".repeat(PlantNameValidator.MAX_LENGTH + 1))
        }
    }
}
