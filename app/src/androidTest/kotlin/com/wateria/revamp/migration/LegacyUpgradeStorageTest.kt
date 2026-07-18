package com.wateria.revamp.migration

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.wateria.data.database.WateriaDatabase
import com.wateria.data.database.entity.PlantEntity
import com.wateria.data.migration.AndroidLegacyPreferencesSource
import com.wateria.data.migration.LegacyMigrationFailureReason
import com.wateria.data.migration.LegacyMigrationInputConverter
import com.wateria.data.migration.LegacyMigrationResult
import com.wateria.data.migration.LegacyMigrationRunner
import com.wateria.data.migration.LegacyPlantParser
import com.wateria.data.migration.LegacySettingsConverter
import com.wateria.data.migration.NoOpMigrationCheckpoint
import com.wateria.data.preferences.LegacyMigrationState
import com.wateria.data.preferences.PreferencesSnapshot
import com.wateria.data.preferences.WateriaPreferencesDataSource
import com.wateria.domain.time.ClockTimeProvider
import java.time.Clock
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Verifies the real Android storage transition after the legacy APK is replaced in place. */
@RunWith(AndroidJUnit4::class)
class LegacyUpgradeStorageTest {
    @Test
    fun migratedStorageMatchesTheLegacySnapshotAndIsIdempotent() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val scenario = currentLegacyUpgradeScenario()
        val expectedLegacyValues = legacyValuesFor(instrumentation.context, scenario)
        val rawPreferences =
            context.getSharedPreferences(LEGACY_PREFERENCES_FILE, Context.MODE_PRIVATE)
        val databaseExistedBeforeVerification =
            context.getDatabasePath(MODERN_DATABASE_FILE).exists()
        val dataStoreExistedBeforeVerification =
            context.preferencesDataStoreFile("wateria.preferences_pb").exists()

        assertEquals(expectedLegacyValues, rawPreferences.all)
        if (scenario == LegacyUpgradeScenario.REPRESENTATIVE) {
            assertTrue(databaseExistedBeforeVerification)
            assertTrue(dataStoreExistedBeforeVerification)
        } else {
            assertFalse(databaseExistedBeforeVerification)
            assertFalse(dataStoreExistedBeforeVerification)
        }

        val dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val database =
            Room.databaseBuilder(context, WateriaDatabase::class.java, MODERN_DATABASE_FILE)
                .build()
        val preferences =
            WateriaPreferencesDataSource(
                PreferenceDataStoreFactory.create(
                    scope = dataStoreScope,
                    produceFile = { context.preferencesDataStoreFile("wateria.preferences_pb") }
                )
            )
        val clock = Clock.systemDefaultZone()
        val runner =
            LegacyMigrationRunner(
                database = database,
                legacyPreferences = AndroidLegacyPreferencesSource(context),
                preferences = preferences,
                inputConverter =
                    LegacyMigrationInputConverter(
                        LegacyPlantParser(),
                        LegacySettingsConverter(),
                        ClockTimeProvider(clock),
                        context
                    ),
                timeProvider = ClockTimeProvider(clock),
                checkpoint = NoOpMigrationCheckpoint()
            )

        try {
            val result = runner.run()
            val plants = database.plantDao().getAll().sortedBy(PlantEntity::id)
            val snapshot = preferences.currentSnapshot()

            when (scenario) {
                LegacyUpgradeScenario.MALFORMED ->
                    verifyMalformedResult(result, plants, snapshot)

                LegacyUpgradeScenario.EMPTY -> verifyEmptyResult(result, plants, snapshot)

                LegacyUpgradeScenario.ALL_ICONS -> verifyAllIconsResult(result, plants, snapshot)

                LegacyUpgradeScenario.REPRESENTATIVE ->
                    verifyRepresentativeResult(result, plants, snapshot, LocalDate.now(clock))
            }

            if (
                result is LegacyMigrationResult.Completed ||
                result is LegacyMigrationResult.AlreadyComplete
            ) {
                assertTrue(runner.run() is LegacyMigrationResult.AlreadyComplete)
                assertEquals(plants, database.plantDao().getAll().sortedBy(PlantEntity::id))
                assertEquals(snapshot, preferences.currentSnapshot())
            }

            assertEquals(expectedLegacyValues, rawPreferences.all)
        } finally {
            database.close()
            dataStoreScope.cancel()
        }
    }

    private fun verifyMalformedResult(
        result: LegacyMigrationResult,
        plants: List<PlantEntity>,
        snapshot: PreferencesSnapshot
    ) {
        assertEquals(
            LegacyMigrationFailureReason.INVALID_WHOLE_PAYLOAD,
            (result as LegacyMigrationResult.Failed).reason
        )
        assertTrue(plants.isEmpty())
        assertEquals(LegacyMigrationState.FAILED, snapshot.migrationMetadata.state)
    }

    private fun verifyEmptyResult(
        result: LegacyMigrationResult,
        plants: List<PlantEntity>,
        snapshot: PreferencesSnapshot
    ) {
        assertTrue(result is LegacyMigrationResult.Completed)
        assertTrue(plants.isEmpty())
        assertEquals(0, snapshot.migrationMetadata.migratedPlantCount)
        assertEquals(true, snapshot.reminderSettings.isEnabled)
        assertEquals(18, snapshot.reminderSettings.time.hour)
        assertEquals(0, snapshot.onboardingVersion)
    }

    private fun verifyAllIconsResult(
        result: LegacyMigrationResult,
        plants: List<PlantEntity>,
        snapshot: PreferencesSnapshot
    ) {
        assertTrue(result is LegacyMigrationResult.Completed)
        assertEquals(44, plants.size)
        assertEquals(44, plants.map(PlantEntity::iconKey).toSet().size)
        assertFalse(plants.any { plant -> plant.iconKey == "unknown_legacy" })
        assertEquals(44, snapshot.migrationMetadata.migratedPlantCount)
        assertEquals(0, snapshot.migrationMetadata.repairedPlantCount)
        assertEquals(0, snapshot.migrationMetadata.failedPlantCount)
    }

    private fun verifyRepresentativeResult(
        result: LegacyMigrationResult,
        plants: List<PlantEntity>,
        snapshot: PreferencesSnapshot,
        verificationDate: LocalDate
    ) {
        assertTrue(result is LegacyMigrationResult.AlreadyComplete)
        assertEquals(2, plants.size)
        assertEquals(
            setOf("Living room Monstera", "Áloe"),
            plants.map(PlantEntity::name).toSet()
        )
        assertEquals(
            setOf("common_08_monstera", "cactus_01"),
            plants.map(PlantEntity::iconKey).toSet()
        )
        assertEquals(
            mapOf(
                "Living room Monstera" to LocalDate.of(2026, 7, 20).toEpochDay(),
                "Áloe" to LocalDate.of(2026, 7, 22).toEpochDay()
            ),
            plants.associate { plant -> plant.name to plant.nextWateringEpochDay }
        )
        assertEquals(
            mapOf("Living room Monstera" to 7, "Áloe" to 10),
            plants.associate { plant -> plant.name to plant.wateringIntervalDays }
        )
        assertEquals(true, snapshot.reminderSettings.isEnabled)
        assertEquals(18, snapshot.reminderSettings.time.hour)
        assertEquals(0, snapshot.reminderSettings.time.minute)
        assertEquals(60, snapshot.reminderSettings.snoozeDuration.toMinutes())
        assertEquals(1, snapshot.onboardingVersion)
        assertEquals(5, snapshot.tipProgress.nextTipIndex)
        assertEquals(verificationDate, snapshot.tipProgress.lastTipDate)
        assertEquals(LegacyMigrationState.COMPLETE, snapshot.migrationMetadata.state)
        assertEquals(1, snapshot.migrationMetadata.version)
        assertEquals(2, snapshot.migrationMetadata.migratedPlantCount)
        assertEquals(0, snapshot.migrationMetadata.repairedPlantCount)
        assertEquals(0, snapshot.migrationMetadata.failedPlantCount)
        assertEquals(0, snapshot.migrationMetadata.repairedSettingsCount)
    }
}
