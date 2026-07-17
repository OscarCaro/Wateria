package com.wateria.data.reminders

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.wateria.data.preferences.WateriaPreferencesDataSource
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.time.TimeProvider
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class WorkManagerReminderScheduler
@Inject
constructor(
    private val workManager: WorkManager,
    private val preferences: WateriaPreferencesDataSource,
    private val timeProvider: TimeProvider
) : ReminderScheduler {
    override suspend fun scheduleNextReminder() {
        val settings = preferences.reminderSettings.first()
        if (!settings.isEnabled) {
            cancelDailyReminder()
            return
        }

        val delay =
            delayUntilNextOccurrence(
                now = ZonedDateTime.ofInstant(timeProvider.instant(), timeProvider.zoneId()),
                reminderTime = settings.time
            )
        val request =
            OneTimeWorkRequestBuilder<DailyReminderWorker>()
                .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
                .addTag(DAILY_REMINDER_TAG)
                .build()
        workManager.enqueueUniqueWork(DAILY_REMINDER_WORK, ExistingWorkPolicy.REPLACE, request)
    }

    override suspend fun cancelDailyReminder() {
        workManager.cancelUniqueWork(DAILY_REMINDER_WORK)
    }

    override suspend fun scheduleSnooze(duration: Duration) {
        require(!duration.isNegative && !duration.isZero) { "Snooze duration must be positive" }
        val request =
            OneTimeWorkRequestBuilder<SnoozeReminderWorker>()
                .setInitialDelay(duration.toMillis(), TimeUnit.MILLISECONDS)
                .addTag(SNOOZE_REMINDER_TAG)
                .build()
        workManager.enqueueUniqueWork(SNOOZE_REMINDER_WORK, ExistingWorkPolicy.REPLACE, request)
    }

    override suspend fun cancelSnooze() {
        workManager.cancelUniqueWork(SNOOZE_REMINDER_WORK)
    }

    companion object {
        const val DAILY_REMINDER_WORK = "wateria_daily_reminder"
        const val SNOOZE_REMINDER_WORK = "wateria_snooze_reminder"
        const val DAILY_REMINDER_TAG = "daily_reminder"
        const val SNOOZE_REMINDER_TAG = "snooze_reminder"
    }
}

internal fun delayUntilNextOccurrence(now: ZonedDateTime, reminderTime: LocalTime): Duration {
    val today = now.toLocalDate().atTime(reminderTime).atZone(now.zone)
    val next = if (today.isAfter(now)) today else today.plusDays(1)
    return Duration.between(now, next)
}
