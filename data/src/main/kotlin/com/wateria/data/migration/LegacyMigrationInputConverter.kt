package com.wateria.data.migration

import android.content.Context
import com.wateria.data.R
import com.wateria.domain.time.TimeProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

sealed interface ConvertedLegacyInput {
    data class Success(
        val plants: LegacyPlantParseResult.Success,
        val settings: ConvertedLegacySettings
    ) : ConvertedLegacyInput

    data object WholePayloadFailure : ConvertedLegacyInput
}

class LegacyMigrationInputConverter
@Inject
constructor(
    private val plantParser: LegacyPlantParser,
    private val settingsConverter: LegacySettingsConverter,
    private val timeProvider: TimeProvider,
    @param:ApplicationContext private val context: Context
) {
    fun convert(
        snapshot: LegacyPreferencesSnapshot,
        migrationTimestamp: Long
    ): ConvertedLegacyInput {
        val rawPlantsValue = snapshot[LegacyPreferenceKeys.PLANT_LIST]
        val hasWrongPayloadType =
            snapshot.contains(LegacyPreferenceKeys.PLANT_LIST) && rawPlantsValue !is String
        if (hasWrongPayloadType) return ConvertedLegacyInput.WholePayloadFailure

        return when (
            val plants =
                plantParser.parse(
                    rawPayload = rawPlantsValue as? String,
                    migrationDate = migrationDate(migrationTimestamp),
                    recoveryName = context.getString(R.string.legacy_recovered_plant_name)
                )
        ) {
            is LegacyPlantParseResult.Success ->
                ConvertedLegacyInput.Success(plants, settingsConverter.convert(snapshot))

            LegacyPlantParseResult.WholePayloadFailure ->
                ConvertedLegacyInput.WholePayloadFailure
        }
    }

    private fun migrationDate(migrationTimestamp: Long): LocalDate =
        LocalDate.ofInstant(Instant.ofEpochMilli(migrationTimestamp), timeProvider.zoneId())
}
