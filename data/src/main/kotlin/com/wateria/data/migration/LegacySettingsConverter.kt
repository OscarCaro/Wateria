package com.wateria.data.migration

import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.model.TipProgress
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

data class ConvertedLegacySettings(
    val reminderSettings: ReminderSettings,
    val onboardingVersion: Int,
    val tipProgress: TipProgress,
    val repairedSettingsCount: Int
)

class LegacySettingsConverter @Inject constructor() {
    fun convert(snapshot: LegacyPreferencesSnapshot): ConvertedLegacySettings {
        val repairs = RepairTracker()
        val remindersEnabled =
            readBoolean(
                snapshot,
                LegacyPreferenceKeys.NOTIFICATIONS_ENABLED,
                DEFAULT_REMINDERS_ENABLED,
                repairs
            )
        val hour =
            readInt(
                snapshot,
                LegacyPreferenceKeys.NOTIFICATION_HOUR,
                MIN_HOUR..MAX_HOUR,
                DEFAULT_REMINDER_HOUR,
                repairs
            )
        val minute =
            readInt(
                snapshot,
                LegacyPreferenceKeys.NOTIFICATION_MINUTE,
                MIN_MINUTE..MAX_MINUTE,
                DEFAULT_REMINDER_MINUTE,
                repairs
            )
        val repetitionHours =
            readInt(
                snapshot,
                LegacyPreferenceKeys.NOTIFICATION_REPETITION_HOURS,
                MIN_SNOOZE_HOURS..MAX_SNOOZE_HOURS,
                DEFAULT_SNOOZE_HOURS,
                repairs
            )
        val firstTime =
            readOptionalBoolean(snapshot, LegacyPreferenceKeys.FIRST_TIME, repairs)
        val tipIndex =
            readInt(
                snapshot,
                LegacyPreferenceKeys.TIP_INDEX,
                MIN_TIP_INDEX..Int.MAX_VALUE,
                DEFAULT_TIP_INDEX,
                repairs
            )

        return ConvertedLegacySettings(
            reminderSettings =
                ReminderSettings(
                    isEnabled = remindersEnabled,
                    time = LocalTime.of(hour, minute),
                    snoozeDuration = Duration.ofHours(repetitionHours.toLong())
                ),
            onboardingVersion = if (firstTime == false) PARITY_ONBOARDING_VERSION else 0,
            tipProgress = TipProgress(nextTipIndex = tipIndex.mod(TIP_COUNT), lastTipDate = null),
            repairedSettingsCount = repairs.count
        )
    }

    private fun readBoolean(
        snapshot: LegacyPreferencesSnapshot,
        key: String,
        default: Boolean,
        repairs: RepairTracker
    ): Boolean = readOptionalBoolean(snapshot, key, repairs) ?: default

    private fun readOptionalBoolean(
        snapshot: LegacyPreferencesSnapshot,
        key: String,
        repairs: RepairTracker
    ): Boolean? {
        val legacyValue = typedValue<Boolean>(snapshot, key)
        repairs.record(legacyValue.wasInvalid)
        return legacyValue.value
    }

    private fun readInt(
        snapshot: LegacyPreferencesSnapshot,
        key: String,
        validRange: IntRange,
        default: Int,
        repairs: RepairTracker
    ): Int {
        val legacyValue = typedValue<Int>(snapshot, key)
        val rangeInvalid = legacyValue.value?.let { value -> value !in validRange } == true
        repairs.record(legacyValue.wasInvalid || rangeInvalid)
        return legacyValue.value?.takeIf { value -> value in validRange } ?: default
    }

    private inline fun <reified T> typedValue(
        snapshot: LegacyPreferencesSnapshot,
        key: String
    ): TypedLegacyValue<T> {
        if (!snapshot.contains(key)) return TypedLegacyValue(value = null, wasInvalid = false)
        val value = snapshot[key]
        return TypedLegacyValue(value = value as? T, wasInvalid = value !is T)
    }

    private data class TypedLegacyValue<T>(val value: T?, val wasInvalid: Boolean)

    private class RepairTracker {
        var count: Int = 0
            private set

        fun record(wasRepaired: Boolean) {
            if (wasRepaired) count = 1
        }
    }

    private companion object {
        const val DEFAULT_REMINDERS_ENABLED = true
        const val DEFAULT_REMINDER_HOUR = 18
        const val DEFAULT_REMINDER_MINUTE = 0
        const val DEFAULT_SNOOZE_HOURS = 1
        const val PARITY_ONBOARDING_VERSION = 1
        const val TIP_COUNT = 7
        const val MIN_HOUR = 0
        const val MAX_HOUR = 23
        const val MIN_MINUTE = 0
        const val MAX_MINUTE = 59
        const val MIN_SNOOZE_HOURS = 1
        const val MAX_SNOOZE_HOURS = 23
        const val MIN_TIP_INDEX = 0
        const val DEFAULT_TIP_INDEX = 0
    }
}
