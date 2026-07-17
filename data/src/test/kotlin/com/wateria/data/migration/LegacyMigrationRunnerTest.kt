package com.wateria.data.migration

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.wateria.data.database.WateriaDatabase
import com.wateria.data.database.entity.PlantEntity
import com.wateria.data.preferences.LegacyMigrationState
import com.wateria.data.preferences.WateriaPreferencesDataSource
import com.wateria.domain.time.TimeProvider
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LegacyMigrationRunnerTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `empty legacy preferences are a successful empty installation`() = runTest {
        val harness = harness(emptyMap())

        try {
            val result = harness.runner.run() as LegacyMigrationResult.Completed

            assertEquals(0, result.metadata.migratedPlantCount)
            assertEquals(LegacyMigrationState.COMPLETE, result.metadata.state)
            assertTrue(harness.database.plantDao().getAll().isEmpty())
            assertEquals(true, harness.preferences.currentSnapshot().reminderSettings.isEnabled)
        } finally {
            harness.database.close()
        }
    }

    @Test
    fun `migration writes Room and DataStore verifies them and then becomes a no-op`() = runTest {
        val legacyValues =
            mapOf<String, Any?>(
                "plantlistkey" to
                    JSONArray()
                        .put(validPlant("Monstera", 0x7f080081))
                        .put(validPlant("Áloe", 0x7f080071))
                        .toString(),
                "notif_enabled" to false,
                "notif_hour" to 9,
                "notif_minute" to 45,
                "notif_repetition" to 3,
                "first_time" to false,
                "tip_idx" to 8,
                "last_day" to 198
            )
        val harness = harness(legacyValues)

        try {
            val result = harness.runner.run()

            assertTrue(result is LegacyMigrationResult.Completed)
            val entities = harness.database.plantDao().getAll()
            assertEquals(2, entities.size)
            assertEquals(
                setOf("common_08_monstera", "cactus_01"),
                entities.map {
                    it.iconKey
                }.toSet()
            )
            assertEquals(2, entities.map { it.id }.toSet().size)

            val stored = harness.preferences.currentSnapshot()
            assertEquals(false, stored.reminderSettings.isEnabled)
            assertEquals(9, stored.reminderSettings.time.hour)
            assertEquals(45, stored.reminderSettings.time.minute)
            assertEquals(1, stored.onboardingVersion)
            assertEquals(1, stored.tipProgress.nextTipIndex)
            assertEquals(null, stored.tipProgress.lastTipDate)
            assertEquals(LegacyMigrationState.COMPLETE, stored.migrationMetadata.state)
            assertEquals(1, stored.migrationMetadata.version)
            assertEquals(2, stored.migrationMetadata.migratedPlantCount)

            assertTrue(harness.runner.run() is LegacyMigrationResult.AlreadyComplete)
            assertEquals(2, harness.database.plantDao().getAll().size)
            assertEquals(legacyValues, harness.legacySnapshot.values)
        } finally {
            harness.database.close()
        }
    }

    @Test
    fun `retry after Room write is idempotent and retains migration timestamp`() = runTest {
        val legacyValues =
            mapOf<String, Any?>(
                "plantlistkey" to
                    JSONArray()
                        .put(validPlant("Interrupted", 0x7f080071).put("month", 13))
                        .toString()
            )
        val timeProvider = MutableTimeProvider()
        val checkpoint = FailOnceAfterRoomWrite()
        val harness = harness(legacyValues, timeProvider, checkpoint)

        try {
            val first = harness.runner.run()

            assertEquals(
                LegacyMigrationFailureReason.INTERRUPTED,
                (first as LegacyMigrationResult.Failed).reason
            )
            val firstEntity = harness.database.plantDao().getAll().single()
            assertEquals(
                LegacyMigrationState.FAILED,
                harness.preferences.migrationMetadata.first().state
            )

            timeProvider.currentInstant = timeProvider.currentInstant.plusSeconds(86_400)
            val retryRunner = harness.runner(checkpoint = NoOpMigrationCheckpoint())
            assertTrue(retryRunner.run() is LegacyMigrationResult.Completed)

            val retriedEntity = harness.database.plantDao().getAll().single()
            assertEquals(firstEntity, retriedEntity)
        } finally {
            harness.database.close()
        }
    }

    @Test
    fun `invalid whole payload preserves existing database and enters recovery state`() = runTest {
        val legacyValues = mapOf<String, Any?>("plantlistkey" to "{not-an-array}")
        val harness = harness(legacyValues)
        val existing =
            PlantEntity(
                id = "00000000-0000-0000-0000-000000000001",
                name = "Existing new record",
                iconKey = "cactus_01",
                wateringIntervalDays = 5,
                nextWateringEpochDay = LocalDate.of(2026, 7, 20).toEpochDay(),
                createdAtEpochMillis = 1,
                updatedAtEpochMillis = 1
            )
        harness.database.plantDao().upsertAll(listOf(existing))

        try {
            val result = harness.runner.run() as LegacyMigrationResult.Failed

            assertEquals(LegacyMigrationFailureReason.INVALID_WHOLE_PAYLOAD, result.reason)
            assertEquals(listOf(existing), harness.database.plantDao().getAll())
            assertEquals(
                LegacyMigrationState.FAILED,
                harness.preferences.migrationMetadata.first().state
            )
            assertEquals(legacyValues, harness.legacySnapshot.values)
        } finally {
            harness.database.close()
        }
    }

    private fun TestScope.harness(
        legacyValues: Map<String, Any?>,
        timeProvider: MutableTimeProvider = MutableTimeProvider(),
        checkpoint: MigrationCheckpoint = NoOpMigrationCheckpoint()
    ): MigrationHarness {
        val database =
            Room.inMemoryDatabaseBuilder(context, WateriaDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        val dataStore =
            PreferenceDataStoreFactory.create(
                scope = backgroundScope,
                produceFile = {
                    File(temporaryFolder.newFolder(), "wateria.preferences_pb")
                }
            )
        val preferences = WateriaPreferencesDataSource(dataStore)
        val legacySnapshot = LegacyPreferencesSnapshot(legacyValues)
        val source = LegacyPreferencesSource { legacySnapshot }
        val runner =
            LegacyMigrationRunner(
                database = database,
                legacyPreferences = source,
                preferences = preferences,
                inputConverter = inputConverter(timeProvider),
                timeProvider = timeProvider,
                checkpoint = checkpoint
            )
        return MigrationHarness(
            database = database,
            preferences = preferences,
            legacySnapshot = legacySnapshot,
            source = source,
            timeProvider = timeProvider,
            runner = runner
        )
    }

    private fun inputConverter(timeProvider: TimeProvider): LegacyMigrationInputConverter =
        LegacyMigrationInputConverter(
            LegacyPlantParser(),
            LegacySettingsConverter(),
            timeProvider,
            context
        )

    private fun validPlant(name: String, icon: Int): JSONObject = JSONObject()
        .put("name", name)
        .put("icon", icon)
        .put("day", 20)
        .put("month", 7)
        .put("year", 2026)
        .put("wat_freq", 5)

    private inner class MigrationHarness(
        val database: WateriaDatabase,
        val preferences: WateriaPreferencesDataSource,
        val legacySnapshot: LegacyPreferencesSnapshot,
        private val source: LegacyPreferencesSource,
        private val timeProvider: MutableTimeProvider,
        val runner: LegacyMigrationRunner
    ) {
        fun runner(checkpoint: MigrationCheckpoint): LegacyMigrationRunner = LegacyMigrationRunner(
            database = database,
            legacyPreferences = source,
            preferences = preferences,
            inputConverter = inputConverter(timeProvider),
            timeProvider = timeProvider,
            checkpoint = checkpoint
        )
    }
}

private class MutableTimeProvider : TimeProvider {
    var currentInstant: Instant = Instant.parse("2026-07-17T10:00:00Z")

    override fun instant(): Instant = currentInstant

    override fun today(): LocalDate = LocalDate.ofInstant(currentInstant, ZoneOffset.UTC)

    override fun zoneId(): ZoneId = ZoneOffset.UTC
}

private class FailOnceAfterRoomWrite : MigrationCheckpoint {
    private var shouldFail = true

    override suspend fun afterRoomWrite() {
        if (shouldFail) {
            shouldFail = false
            error("Simulated interruption")
        }
    }
}
