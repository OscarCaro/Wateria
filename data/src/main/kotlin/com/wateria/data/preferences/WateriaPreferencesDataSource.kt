package com.wateria.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.model.TipProgress
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class WateriaPreferencesDataSource
@Inject
constructor(
    private val dataStore: DataStore<Preferences>
) {
    val reminderSettings: Flow<ReminderSettings> =
        dataStore.data.map { preferences -> preferences.toReminderSettings() }

    val onboardingVersion: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[Keys.ONBOARDING_VERSION_COMPLETED] ?: 0
        }

    val tipProgress: Flow<TipProgress> =
        dataStore.data.map { preferences -> preferences.toTipProgress() }

    val migrationMetadata: Flow<LegacyMigrationMetadata> =
        dataStore.data.map { preferences -> preferences.toMigrationMetadata() }

    suspend fun updateReminderSettings(settings: ReminderSettings) {
        dataStore.edit { preferences -> preferences.writeReminderSettings(settings) }
    }

    suspend fun setOnboardingVersion(version: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.ONBOARDING_VERSION_COMPLETED] = version
        }
    }

    suspend fun updateTipProgress(progress: TipProgress) {
        dataStore.edit { preferences -> preferences.writeTipProgress(progress) }
    }

    suspend fun markMigrationInProgress(startedAtEpochMillis: Long): Long {
        var retainedTimestamp = startedAtEpochMillis
        dataStore.edit { preferences ->
            retainedTimestamp =
                preferences[Keys.LEGACY_MIGRATION_STARTED_AT] ?: startedAtEpochMillis
            preferences[Keys.LEGACY_MIGRATION_STARTED_AT] = retainedTimestamp
            preferences[Keys.LEGACY_MIGRATION_STATE] =
                LegacyMigrationState.IN_PROGRESS.storedValue
        }
        return retainedTimestamp
    }

    suspend fun writeMigratedValues(
        reminderSettings: ReminderSettings,
        onboardingVersion: Int,
        tipProgress: TipProgress
    ) {
        dataStore.edit { preferences ->
            preferences.writeReminderSettings(reminderSettings)
            preferences[Keys.ONBOARDING_VERSION_COMPLETED] = onboardingVersion
            preferences.writeTipProgress(tipProgress)
        }
    }

    suspend fun markMigrationComplete(metadata: LegacyMigrationMetadata) {
        dataStore.edit { preferences ->
            preferences.writeMigrationMetadata(
                metadata.copy(
                    state = LegacyMigrationState.COMPLETE,
                    version = CURRENT_MIGRATION_VERSION
                )
            )
        }
    }

    suspend fun markMigrationFailed(metadata: LegacyMigrationMetadata) {
        dataStore.edit { preferences ->
            preferences.writeMigrationMetadata(
                metadata.copy(state = LegacyMigrationState.FAILED)
            )
        }
    }

    suspend fun currentSnapshot(): PreferencesSnapshot = PreferencesSnapshot(
        reminderSettings = reminderSettings.first(),
        onboardingVersion = onboardingVersion.first(),
        tipProgress = tipProgress.first(),
        migrationMetadata = migrationMetadata.first()
    )

    companion object {
        const val CURRENT_MIGRATION_VERSION = 1
    }
}

private fun Preferences.toReminderSettings(): ReminderSettings {
    val hour =
        (this[Keys.REMINDER_HOUR] ?: DEFAULT_REMINDER_HOUR).coerceIn(MIN_HOUR, MAX_HOUR)
    val minute =
        (this[Keys.REMINDER_MINUTE] ?: DEFAULT_REMINDER_MINUTE).coerceIn(MIN_MINUTE, MAX_MINUTE)
    val snoozeMinutes =
        (this[Keys.SNOOZE_DURATION_MINUTES] ?: DEFAULT_SNOOZE_MINUTES)
            .coerceAtLeast(MIN_SNOOZE_MINUTES)
    return ReminderSettings(
        isEnabled = this[Keys.REMINDERS_ENABLED] ?: true,
        time = LocalTime.of(hour, minute),
        snoozeDuration = Duration.ofMinutes(snoozeMinutes.toLong())
    )
}

private fun Preferences.toTipProgress(): TipProgress = TipProgress(
    nextTipIndex = (this[Keys.NEXT_TIP_INDEX] ?: DEFAULT_TIP_INDEX).coerceAtLeast(
        DEFAULT_TIP_INDEX
    ),
    lastTipDate = this[Keys.LAST_TIP_EPOCH_DAY]?.let(LocalDate::ofEpochDay)
)

private fun Preferences.toMigrationMetadata(): LegacyMigrationMetadata = LegacyMigrationMetadata(
    state = LegacyMigrationState.fromStoredValue(this[Keys.LEGACY_MIGRATION_STATE]),
    version = this[Keys.LEGACY_MIGRATION_VERSION] ?: 0,
    startedAtEpochMillis = this[Keys.LEGACY_MIGRATION_STARTED_AT],
    migratedPlantCount = this[Keys.LEGACY_MIGRATED_PLANT_COUNT] ?: 0,
    repairedPlantCount = this[Keys.LEGACY_REPAIRED_PLANT_COUNT] ?: 0,
    failedPlantCount = this[Keys.LEGACY_FAILED_PLANT_COUNT] ?: 0,
    repairedSettingsCount = this[Keys.LEGACY_REPAIRED_SETTINGS_COUNT] ?: 0
)

private fun MutablePreferences.writeReminderSettings(settings: ReminderSettings) {
    this[Keys.REMINDERS_ENABLED] = settings.isEnabled
    this[Keys.REMINDER_HOUR] = settings.time.hour
    this[Keys.REMINDER_MINUTE] = settings.time.minute
    this[Keys.SNOOZE_DURATION_MINUTES] = settings.snoozeDuration.toMinutes().toInt()
}

private fun MutablePreferences.writeTipProgress(progress: TipProgress) {
    this[Keys.NEXT_TIP_INDEX] = progress.nextTipIndex
    progress.lastTipDate?.let { lastTipDate ->
        this[Keys.LAST_TIP_EPOCH_DAY] = lastTipDate.toEpochDay()
    } ?: remove(Keys.LAST_TIP_EPOCH_DAY)
}

private fun MutablePreferences.writeMigrationMetadata(metadata: LegacyMigrationMetadata) {
    this[Keys.LEGACY_MIGRATION_STATE] = metadata.state.storedValue
    this[Keys.LEGACY_MIGRATION_VERSION] = metadata.version
    metadata.startedAtEpochMillis?.let { timestamp ->
        this[Keys.LEGACY_MIGRATION_STARTED_AT] = timestamp
    }
    this[Keys.LEGACY_MIGRATED_PLANT_COUNT] = metadata.migratedPlantCount
    this[Keys.LEGACY_REPAIRED_PLANT_COUNT] = metadata.repairedPlantCount
    this[Keys.LEGACY_FAILED_PLANT_COUNT] = metadata.failedPlantCount
    this[Keys.LEGACY_REPAIRED_SETTINGS_COUNT] = metadata.repairedSettingsCount
}

private object Keys {
    val REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
    val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    val SNOOZE_DURATION_MINUTES = intPreferencesKey("snooze_duration_minutes")
    val ONBOARDING_VERSION_COMPLETED = intPreferencesKey("onboarding_version_completed")
    val NEXT_TIP_INDEX = intPreferencesKey("next_tip_index")
    val LAST_TIP_EPOCH_DAY = longPreferencesKey("last_tip_epoch_day")
    val LEGACY_MIGRATION_STATE = stringPreferencesKey("legacy_migration_state")
    val LEGACY_MIGRATION_VERSION = intPreferencesKey("legacy_migration_version")
    val LEGACY_MIGRATION_STARTED_AT =
        longPreferencesKey("legacy_migration_started_at_epoch_millis")
    val LEGACY_MIGRATED_PLANT_COUNT = intPreferencesKey("legacy_migrated_plant_count")
    val LEGACY_REPAIRED_PLANT_COUNT = intPreferencesKey("legacy_repaired_plant_count")
    val LEGACY_FAILED_PLANT_COUNT = intPreferencesKey("legacy_failed_plant_count")
    val LEGACY_REPAIRED_SETTINGS_COUNT = intPreferencesKey("legacy_repaired_settings_count")
}

private const val DEFAULT_REMINDER_HOUR = 18
private const val DEFAULT_REMINDER_MINUTE = 0
private const val DEFAULT_SNOOZE_MINUTES = 60
private const val MIN_HOUR = 0
private const val MAX_HOUR = 23
private const val MIN_MINUTE = 0
private const val MAX_MINUTE = 59
private const val MIN_SNOOZE_MINUTES = 1
private const val DEFAULT_TIP_INDEX = 0

data class PreferencesSnapshot(
    val reminderSettings: ReminderSettings,
    val onboardingVersion: Int,
    val tipProgress: TipProgress,
    val migrationMetadata: LegacyMigrationMetadata
)
