package com.wateria.domain.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

private const val DEFAULT_REMINDER_HOUR = 18
private const val DEFAULT_REMINDER_MINUTE = 0
private const val DEFAULT_SNOOZE_HOURS = 1L

data class ReminderSettings(
    val isEnabled: Boolean = true,
    val time: LocalTime = LocalTime.of(DEFAULT_REMINDER_HOUR, DEFAULT_REMINDER_MINUTE),
    val snoozeDuration: Duration = Duration.ofHours(DEFAULT_SNOOZE_HOURS)
)

data class TipProgress(val nextTipIndex: Int = 0, val lastTipDate: LocalDate? = null)
