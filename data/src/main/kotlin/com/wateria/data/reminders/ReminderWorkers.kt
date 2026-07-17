package com.wateria.data.reminders

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.usecase.GetDuePlantsUseCase
import com.wateria.domain.usecase.ObserveReminderSettingsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

@HiltWorker
class DailyReminderWorker
@AssistedInject
constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getDuePlants: GetDuePlantsUseCase,
    private val observeReminderSettings: ObserveReminderSettingsUseCase,
    private val scheduler: ReminderScheduler,
    private val publisher: ReminderNotificationPublisher
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        var settings: ReminderSettings? = null
        return try {
            settings = observeReminderSettings().first()
            if (settings.isEnabled) {
                val duePlants = getDuePlants()
                if (duePlants.isEmpty()) publisher.cancel() else publisher.show(duePlants)
            } else {
                publisher.cancel()
            }
            Result.success()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            Result.retry()
        } finally {
            if (settings?.isEnabled == true) {
                runCatching { scheduler.scheduleNextReminderAfterCurrent() }
            }
        }
    }
}

@HiltWorker
class SnoozeReminderWorker
@AssistedInject
constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getDuePlants: GetDuePlantsUseCase,
    private val observeReminderSettings: ObserveReminderSettingsUseCase,
    private val publisher: ReminderNotificationPublisher
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = try {
        val settings = observeReminderSettings().first()
        if (settings.isEnabled) {
            val duePlants = getDuePlants()
            if (duePlants.isEmpty()) publisher.cancel() else publisher.show(duePlants)
        } else {
            publisher.cancel()
        }
        Result.success()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (_: Exception) {
        Result.retry()
    }
}
