package com.wateria.data.preferences

enum class LegacyMigrationState(val storedValue: String) {
    NOT_STARTED("not_started"),
    IN_PROGRESS("in_progress"),
    COMPLETE("complete"),
    FAILED("failed");

    companion object {
        fun fromStoredValue(value: String?): LegacyMigrationState =
            entries.firstOrNull { state -> state.storedValue == value } ?: NOT_STARTED
    }
}

data class LegacyMigrationMetadata(
    val state: LegacyMigrationState = LegacyMigrationState.NOT_STARTED,
    val version: Int = 0,
    val startedAtEpochMillis: Long? = null,
    val migratedPlantCount: Int = 0,
    val repairedPlantCount: Int = 0,
    val failedPlantCount: Int = 0,
    val repairedSettingsCount: Int = 0
)
