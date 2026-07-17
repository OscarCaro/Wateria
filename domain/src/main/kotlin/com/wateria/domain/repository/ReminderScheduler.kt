package com.wateria.domain.repository

import java.time.Duration

interface ReminderScheduler {
    suspend fun scheduleNextReminder()

    suspend fun cancelDailyReminder()

    suspend fun scheduleSnooze(duration: Duration)

    suspend fun cancelSnooze()
}
