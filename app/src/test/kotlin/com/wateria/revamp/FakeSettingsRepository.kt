package com.wateria.revamp

import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.model.TipProgress
import com.wateria.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository : SettingsRepository {
    val reminders = MutableStateFlow(ReminderSettings())
    val onboardingVersion = MutableStateFlow(0)
    val tipProgress = MutableStateFlow(TipProgress())

    override fun observeReminderSettings(): Flow<ReminderSettings> = reminders

    override suspend fun updateReminderSettings(settings: ReminderSettings) {
        reminders.value = settings
    }

    override fun observeOnboardingVersion(): Flow<Int> = onboardingVersion

    override suspend fun setOnboardingVersion(version: Int) {
        onboardingVersion.value = version
    }

    override fun observeTipProgress(): Flow<TipProgress> = tipProgress

    override suspend fun updateTipProgress(progress: TipProgress) {
        tipProgress.value = progress
    }
}
