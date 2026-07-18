package com.wateria.revamp.migration

import android.content.Context
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONObject

internal enum class LegacyUpgradeScenario(val argumentValue: String) {
    MALFORMED("malformed"),
    EMPTY("empty"),
    ALL_ICONS("all_icons"),
    REPRESENTATIVE("representative");

    companion object {
        fun from(arguments: Bundle): LegacyUpgradeScenario {
            val requested = arguments.getString(SCENARIO_ARGUMENT) ?: REPRESENTATIVE.argumentValue
            return entries.firstOrNull { scenario -> scenario.argumentValue == requested }
                ?: error("Unknown Phase 7A scenario: $requested")
        }
    }
}

internal fun currentLegacyUpgradeScenario(): LegacyUpgradeScenario =
    LegacyUpgradeScenario.from(InstrumentationRegistry.getArguments())

internal fun legacyValuesFor(
    testContext: Context,
    scenario: LegacyUpgradeScenario
): Map<String, Any> = when (scenario) {
    LegacyUpgradeScenario.MALFORMED ->
        mapOf(PLANT_LIST_KEY to testContext.readLegacyAsset("malformed.json"))

    LegacyUpgradeScenario.EMPTY -> emptyMap()

    LegacyUpgradeScenario.ALL_ICONS ->
        mapOf(PLANT_LIST_KEY to testContext.readLegacyAsset("all_icons.json"))

    LegacyUpgradeScenario.REPRESENTATIVE ->
        JSONObject(testContext.readLegacyAsset("representative_preferences.json"))
            .toTypedMap()
}

private fun Context.readLegacyAsset(fileName: String): String =
    assets.open("legacy/$fileName").bufferedReader().use { reader -> reader.readText().trimEnd() }

private fun JSONObject.toTypedMap(): Map<String, Any> = buildMap {
    keys().forEach { key ->
        val value = requireNotNull(this@toTypedMap.get(key))
        require(value is String || value is Boolean || value is Int) {
            "Unsupported legacy fixture type for $key: ${value::class.java.name}"
        }
        put(key, value)
    }
}

internal const val SCENARIO_ARGUMENT = "scenario"
internal const val LEGACY_PREFERENCES_FILE = "com.wateria.debug_preferences"
internal const val PLANT_LIST_KEY = "plantlistkey"
internal const val MODERN_DATABASE_FILE = "wateria.db"
internal const val MODERN_DATASTORE_FILE = "datastore/wateria.preferences_pb"
