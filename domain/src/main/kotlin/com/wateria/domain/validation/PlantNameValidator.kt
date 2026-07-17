package com.wateria.domain.validation

sealed class InvalidPlantNameException(message: String) : IllegalArgumentException(message) {
    data object Blank : InvalidPlantNameException("Plant name cannot be blank")

    data object TooLong :
        InvalidPlantNameException(
            "Plant name cannot be longer than ${PlantNameValidator.MAX_LENGTH} characters"
        )
}

object PlantNameValidator {
    const val MAX_LENGTH = 50

    fun normalizeAndValidate(input: String): String {
        val normalized = input.trim()
        if (normalized.isEmpty()) throw InvalidPlantNameException.Blank
        if (normalized.length > MAX_LENGTH) throw InvalidPlantNameException.TooLong
        return normalized
    }
}
