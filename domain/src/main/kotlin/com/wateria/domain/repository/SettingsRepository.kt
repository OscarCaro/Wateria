package com.wateria.domain.repository

import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.model.TipProgress
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeReminderSettings(): Flow<ReminderSettings>

    suspend fun updateReminderSettings(settings: ReminderSettings)

    fun observeOnboardingVersion(): Flow<Int>

    suspend fun setOnboardingVersion(version: Int)

    fun observeTipProgress(): Flow<TipProgress>

    suspend fun updateTipProgress(progress: TipProgress)
}
