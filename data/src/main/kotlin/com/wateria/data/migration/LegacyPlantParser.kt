package com.wateria.data.migration

import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.PlantId
import com.wateria.domain.model.WateringInterval
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

sealed interface LegacyPlantParseResult {
    data class Success(
        val plants: List<Plant>,
        val repairedPlantCount: Int,
        val failedPlantCount: Int
    ) : LegacyPlantParseResult

    data object WholePayloadFailure : LegacyPlantParseResult
}

class LegacyPlantParser @Inject constructor() {
    fun parse(
        rawPayload: String?,
        migrationDate: LocalDate,
        recoveryName: String
    ): LegacyPlantParseResult = if (rawPayload == null) {
        LegacyPlantParseResult.Success(emptyList(), 0, 0)
    } else {
        decodeArray(rawPayload)?.let { array ->
            parseArray(array, migrationDate, recoveryName)
        } ?: LegacyPlantParseResult.WholePayloadFailure
    }

    private fun decodeArray(rawPayload: String): JSONArray? = try {
        JSONArray(rawPayload)
    } catch (_: JSONException) {
        null
    }

    private fun parseArray(
        array: JSONArray,
        migrationDate: LocalDate,
        recoveryName: String
    ): LegacyPlantParseResult.Success {
        val plants = mutableListOf<Plant>()
        var repairedCount = 0
        var failedCount = 0

        repeat(array.length()) { index ->
            val value = array.opt(index)
            if (value !is JSONObject) {
                failedCount++
                return@repeat
            }

            val parsed = parsePlant(index, value, migrationDate, recoveryName)
            plants += parsed.plant
            if (parsed.wasRepaired) repairedCount++
        }

        return LegacyPlantParseResult.Success(plants, repairedCount, failedCount)
    }

    private fun parsePlant(
        index: Int,
        source: JSONObject,
        migrationDate: LocalDate,
        recoveryName: String
    ): ParsedPlant {
        var repaired = false

        val rawName = source.opt(FIELD_NAME)
        val name =
            if (rawName is String) {
                rawName
            } else {
                repaired = true
                recoveryName
            }

        val rawFrequency = (source.opt(FIELD_FREQUENCY) as? Number)?.toInt()
        val interval = WateringInterval.clamp(rawFrequency ?: WateringInterval.MIN_DAYS)
        if (rawFrequency == null || rawFrequency != interval.days) repaired = true

        val rawIconId = (source.opt(FIELD_ICON) as? Number)?.toInt()
        val icon = rawIconId?.let(LegacyIconMapper::map) ?: PlantIcon.UnknownLegacy
        if (rawIconId == null || icon == PlantIcon.UnknownLegacy) repaired = true

        val date = parseDate(source)
        val nextWateringDate =
            if (date == null) {
                repaired = true
                migrationDate.plusDays(interval.days.toLong())
            } else {
                date
            }

        val id = deterministicId(index, source)
        return ParsedPlant(
            plant =
                Plant(
                    id = id,
                    name = name,
                    icon = icon,
                    wateringInterval = interval,
                    nextWateringDate = nextWateringDate
                ),
            wasRepaired = repaired
        )
    }

    private fun parseDate(source: JSONObject): LocalDate? {
        val year = (source.opt(FIELD_YEAR) as? Number)?.toInt()
        val month = (source.opt(FIELD_MONTH) as? Number)?.toInt()
        val day = (source.opt(FIELD_DAY) as? Number)?.toInt()
        return if (year == null || month == null || day == null) {
            null
        } else {
            runCatching { LocalDate.of(year, month, day) }.getOrNull()
        }
    }

    private fun deterministicId(index: Int, source: JSONObject): PlantId {
        val seed =
            buildString {
                append(DETERMINISTIC_ID_NAMESPACE)
                append('|')
                append(index)
                for (field in ID_FIELDS) {
                    append('|')
                    append(field)
                    append('=')
                    append(rawToken(source.opt(field)))
                }
            }
        return PlantId.from(UUID.nameUUIDFromBytes(seed.toByteArray(StandardCharsets.UTF_8)))
    }

    private fun rawToken(value: Any?): String = when (value) {
        null, JSONObject.NULL -> "<missing>"
        else -> "${value::class.java.name}:$value"
    }

    private data class ParsedPlant(val plant: Plant, val wasRepaired: Boolean)

    private companion object {
        const val DETERMINISTIC_ID_NAMESPACE = "wateria-v1.6-plant"
        const val FIELD_NAME = "name"
        const val FIELD_ICON = "icon"
        const val FIELD_DAY = "day"
        const val FIELD_MONTH = "month"
        const val FIELD_YEAR = "year"
        const val FIELD_FREQUENCY = "wat_freq"
        val ID_FIELDS =
            listOf(FIELD_NAME, FIELD_ICON, FIELD_DAY, FIELD_MONTH, FIELD_YEAR, FIELD_FREQUENCY)
    }
}
