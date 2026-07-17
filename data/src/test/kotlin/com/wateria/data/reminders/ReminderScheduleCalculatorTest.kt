package com.wateria.data.reminders

import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderScheduleCalculatorTest {
    private val zone = ZoneId.of("Europe/Madrid")

    @Test
    fun `future reminder time stays on the same day`() {
        val now = ZonedDateTime.of(2026, 7, 17, 17, 30, 0, 0, zone)

        assertEquals(
            Duration.ofMinutes(30),
            delayUntilNextOccurrence(now, LocalTime.of(18, 0))
        )
    }

    @Test
    fun `elapsed or exact reminder time rolls to tomorrow`() {
        val now = ZonedDateTime.of(2026, 7, 17, 18, 0, 0, 0, zone)

        assertEquals(
            Duration.ofHours(24),
            delayUntilNextOccurrence(now, LocalTime.of(18, 0))
        )
    }

    @Test
    fun `calendar scheduling respects daylight saving transitions`() {
        val beforeSpringTransition = ZonedDateTime.of(2026, 3, 28, 18, 0, 0, 0, zone)

        assertEquals(
            Duration.ofHours(23),
            delayUntilNextOccurrence(beforeSpringTransition, LocalTime.of(18, 0))
        )
    }
}
