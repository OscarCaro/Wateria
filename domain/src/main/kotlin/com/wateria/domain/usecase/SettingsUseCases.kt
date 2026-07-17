package com.wateria.domain.usecase

import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.model.TipProgress
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

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
        }
    }
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
