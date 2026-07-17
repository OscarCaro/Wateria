package com.wateria.data.migration

import java.time.Duration
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class LegacySettingsConverterTest {
    private val converter = LegacySettingsConverter()

    @Test
    fun `valid legacy settings convert without repair`() {
        val result =
            converter.convert(
                LegacyPreferencesSnapshot(
                    mapOf(
                        "notif_enabled" to false,
                        "notif_hour" to 9,
                        "notif_minute" to 30,
                        "notif_repetition" to 4,
                        "first_time" to false,
                        "tip_idx" to 8,
                        "last_day" to 198
                    )
                )
            )

        assertEquals(false, result.reminderSettings.isEnabled)
        assertEquals(LocalTime.of(9, 30), result.reminderSettings.time)
        assertEquals(Duration.ofHours(4), result.reminderSettings.snoozeDuration)
        assertEquals(1, result.onboardingVersion)
        assertEquals(1, result.tipProgress.nextTipIndex)
        assertEquals(null, result.tipProgress.lastTipDate)
        assertEquals(0, result.repairedSettingsCount)
    }

    @Test
    fun `invalid and wrong type settings use parity defaults and record repair`() {
        val result =
            converter.convert(
                LegacyPreferencesSnapshot(
                    mapOf(
                        "notif_enabled" to "yes",
                        "notif_hour" to -1,
                        "notif_minute" to 60,
                        "notif_repetition" to 24,
                        "tip_idx" to -3
                    )
                )
            )

        assertEquals(true, result.reminderSettings.isEnabled)
        assertEquals(LocalTime.of(18, 0), result.reminderSettings.time)
        assertEquals(Duration.ofHours(1), result.reminderSettings.snoozeDuration)
        assertEquals(0, result.tipProgress.nextTipIndex)
        assertEquals(1, result.repairedSettingsCount)
    }
}
