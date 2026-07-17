package com.wateria.domain.validation

class InvalidPlantNameException : IllegalArgumentException("Plant name cannot be blank")

object PlantNameValidator {
    fun normalizeAndValidate(input: String): String {
        val normalized = input.trim()
        if (normalized.isEmpty()) throw InvalidPlantNameException()
        return normalized
    }
}
