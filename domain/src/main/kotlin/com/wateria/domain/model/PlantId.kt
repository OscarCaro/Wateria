package com.wateria.domain.model

import java.util.UUID

@JvmInline
value class PlantId private constructor(val value: String) {
    override fun toString(): String = value

    companion object {
        fun from(value: String): PlantId = PlantId(UUID.fromString(value).toString())

        fun from(uuid: UUID): PlantId = PlantId(uuid.toString())

        fun parseOrNull(value: String): PlantId? = runCatching { from(value) }.getOrNull()
    }
}
