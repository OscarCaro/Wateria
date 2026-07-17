package com.wateria.domain.usecase

import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.model.TipProgress
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.repository.SettingsRepository
import com.wateria.domain.time.TimeProvider
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ObserveReminderSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<ReminderSettings> = repository.observeReminderSettings()
}

class UpdateReminderSettingsUseCase(
    private val repository: SettingsRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(settings: ReminderSettings) {
        require(!settings.snoozeDuration.isNegative && !settings.snoozeDuration.isZero) {
            "Snooze duration must be positive"
        }
        repository.updateReminderSettings(settings)
        if (settings.isEnabled) {
            scheduler.scheduleNextReminder()
        } else {
            scheduler.cancelDailyReminder()
            scheduler.cancelSnooze()
            scheduler.cancelDisplayedReminder()
        }
    }
}

class ObserveOnboardingVersionUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<Int> = repository.observeOnboardingVersion()
}

class CompleteOnboardingUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(version: Int) {
        require(version > 0) { "Onboarding version must be positive" }
        repository.setOnboardingVersion(version)
    }
}

class ObserveTipProgressUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<TipProgress> = repository.observeTipProgress()
}

class UpdateTipProgressUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(progress: TipProgress) {
        require(progress.nextTipIndex >= 0) { "Tip index cannot be negative" }
        repository.updateTipProgress(progress)
    }
}

class ShouldShowDailyTipUseCase(
    private val repository: SettingsRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(): Boolean =
        repository.observeTipProgress().first().lastTipDate != timeProvider.today()
}

data class DailyTip(val index: Int, val hoursUntilNext: Int, val minutesUntilNext: Int)

class GetDailyTipUseCase(
    private val repository: SettingsRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(tipCount: Int): DailyTip {
        require(tipCount > 0) { "Tip count must be positive" }
        val today = timeProvider.today()
        val progress = repository.observeTipProgress().first()
        val index =
            if (progress.lastTipDate == today) {
                Math.floorMod(progress.nextTipIndex - 1, tipCount)
            } else {
                Math.floorMod(progress.nextTipIndex, tipCount)
            }
        if (progress.lastTipDate != today) {
            repository.updateTipProgress(
                TipProgress(nextTipIndex = progress.nextTipIndex + 1, lastTipDate = today)
            )
        }

        val now = ZonedDateTime.ofInstant(timeProvider.instant(), timeProvider.zoneId())
        val nextDay = today.plusDays(1).atStartOfDay(timeProvider.zoneId())
        val remainingMinutes = java.time.Duration.between(now, nextDay).toMinutes().coerceAtLeast(0)
        return DailyTip(
            index = index,
            hoursUntilNext = (remainingMinutes / MINUTES_PER_HOUR).toInt(),
            minutesUntilNext = (remainingMinutes % MINUTES_PER_HOUR).toInt()
        )
    }

    private companion object {
        const val MINUTES_PER_HOUR = 60L
    }
}
