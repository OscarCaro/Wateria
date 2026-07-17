package com.wateria.data.migration

import androidx.room.withTransaction
import com.wateria.data.database.WateriaDatabase
import com.wateria.data.database.entity.PlantEntity
import com.wateria.data.database.mapper.toEntity
import com.wateria.data.preferences.LegacyMigrationMetadata
import com.wateria.data.preferences.LegacyMigrationState
import com.wateria.data.preferences.WateriaPreferencesDataSource
import com.wateria.domain.time.TimeProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

sealed interface LegacyMigrationResult {
    data class Completed(val metadata: LegacyMigrationMetadata) : LegacyMigrationResult

    data class AlreadyComplete(val metadata: LegacyMigrationMetadata) : LegacyMigrationResult

    data class Failed(
        val reason: LegacyMigrationFailureReason,
        val metadata: LegacyMigrationMetadata
    ) : LegacyMigrationResult
}

enum class LegacyMigrationFailureReason {
    INVALID_WHOLE_PAYLOAD,
    VERIFICATION_FAILED,
    INTERRUPTED
}

interface MigrationCheckpoint {
    suspend fun afterRoomWrite() = Unit

    suspend fun afterPreferencesWrite() = Unit
}

class NoOpMigrationCheckpoint @Inject constructor() : MigrationCheckpoint

@Singleton
class LegacyMigrationRunner
@Inject
constructor(
    private val database: WateriaDatabase,
    private val legacyPreferences: LegacyPreferencesSource,
    private val preferences: WateriaPreferencesDataSource,
    private val inputConverter: LegacyMigrationInputConverter,
    private val timeProvider: TimeProvider,
    private val checkpoint: MigrationCheckpoint
) {
    suspend fun run(): LegacyMigrationResult {
        val currentMetadata = preferences.migrationMetadata.first()
        if (
            currentMetadata.state == LegacyMigrationState.COMPLETE &&
            currentMetadata.version >=
            WateriaPreferencesDataSource.CURRENT_MIGRATION_VERSION
        ) {
            return LegacyMigrationResult.AlreadyComplete(currentMetadata)
        }

        val migrationTimestamp =
            preferences.markMigrationInProgress(timeProvider.instant().toEpochMilli())
        val inProgressMetadata =
            currentMetadata.copy(
                state = LegacyMigrationState.IN_PROGRESS,
                startedAtEpochMillis = migrationTimestamp
            )

        return try {
            migrate(inProgressMetadata, migrationTimestamp)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            val failed = inProgressMetadata.copy(state = LegacyMigrationState.FAILED)
            preferences.markMigrationFailed(failed)
            LegacyMigrationResult.Failed(LegacyMigrationFailureReason.INTERRUPTED, failed)
        }
    }

    private suspend fun migrate(
        inProgressMetadata: LegacyMigrationMetadata,
        migrationTimestamp: Long
    ): LegacyMigrationResult {
        val snapshot = legacyPreferences.read()
        val input = inputConverter.convert(snapshot, migrationTimestamp)
        return when (input) {
            ConvertedLegacyInput.WholePayloadFailure -> failInvalidPayload(inProgressMetadata)

            is ConvertedLegacyInput.Success ->
                persistAndVerify(input, inProgressMetadata, migrationTimestamp)
        }
    }

    private suspend fun persistAndVerify(
        input: ConvertedLegacyInput.Success,
        inProgressMetadata: LegacyMigrationMetadata,
        migrationTimestamp: Long
    ): LegacyMigrationResult {
        val entities =
            input.plants.plants.map { plant ->
                plant.toEntity(
                    createdAtEpochMillis = migrationTimestamp,
                    updatedAtEpochMillis = migrationTimestamp
                )
            }
        database.withTransaction { database.plantDao().upsertAll(entities) }
        checkpoint.afterRoomWrite()

        preferences.writeMigratedValues(
            reminderSettings = input.settings.reminderSettings,
            onboardingVersion = input.settings.onboardingVersion,
            tipProgress = input.settings.tipProgress
        )
        checkpoint.afterPreferencesWrite()

        val completedMetadata =
            inProgressMetadata.copy(
                migratedPlantCount = entities.size,
                repairedPlantCount = input.plants.repairedPlantCount,
                failedPlantCount = input.plants.failedPlantCount,
                repairedSettingsCount = input.settings.repairedSettingsCount
            )

        if (!verify(entities, input.settings)) {
            val failed = completedMetadata.copy(state = LegacyMigrationState.FAILED)
            preferences.markMigrationFailed(failed)
            return LegacyMigrationResult.Failed(
                LegacyMigrationFailureReason.VERIFICATION_FAILED,
                failed
            )
        }

        preferences.markMigrationComplete(completedMetadata)
        val verifiedMetadata = preferences.migrationMetadata.first()
        return LegacyMigrationResult.Completed(verifiedMetadata)
    }

    private suspend fun failInvalidPayload(
        inProgressMetadata: LegacyMigrationMetadata
    ): LegacyMigrationResult {
        val failed = inProgressMetadata.copy(state = LegacyMigrationState.FAILED)
        preferences.markMigrationFailed(failed)
        return LegacyMigrationResult.Failed(
            LegacyMigrationFailureReason.INVALID_WHOLE_PAYLOAD,
            failed
        )
    }

    private suspend fun verify(
        expectedEntities: List<PlantEntity>,
        expectedSettings: ConvertedLegacySettings
    ): Boolean {
        val storedById = database.plantDao().getAll().associateBy(PlantEntity::id)
        val plantsMatch = expectedEntities.all { expected ->
            storedById[expected.id] == expected
        }
        if (!plantsMatch) return false

        val storedPreferences = preferences.currentSnapshot()
        return storedPreferences.reminderSettings == expectedSettings.reminderSettings &&
            storedPreferences.onboardingVersion == expectedSettings.onboardingVersion &&
            storedPreferences.tipProgress == expectedSettings.tipProgress
    }
}
