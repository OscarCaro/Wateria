package com.wateria.data.repository

import com.wateria.data.preferences.WateriaPreferencesDataSource
import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.model.TipProgress
import com.wateria.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DataStoreSettingsRepository
@Inject
constructor(
    private val preferences: WateriaPreferencesDataSource
) : SettingsRepository {
    override fun observeReminderSettings(): Flow<ReminderSettings> = preferences.reminderSettings

    override suspend fun updateReminderSettings(settings: ReminderSettings) {
        preferences.updateReminderSettings(settings)
    }

    override fun observeOnboardingVersion(): Flow<Int> = preferences.onboardingVersion

    override suspend fun setOnboardingVersion(version: Int) {
        preferences.setOnboardingVersion(version)
    }

    override fun observeTipProgress(): Flow<TipProgress> = preferences.tipProgress

    override suspend fun updateTipProgress(progress: TipProgress) {
        preferences.updateTipProgress(progress)
    }
}
