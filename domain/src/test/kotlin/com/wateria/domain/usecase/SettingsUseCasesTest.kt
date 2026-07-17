package com.wateria.domain.usecase

import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.model.TipProgress
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.repository.SettingsRepository
import java.time.Duration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsUseCasesTest {
    private val repository = FakeSettingsRepository()
    private val scheduler = SettingsReminderScheduler()

    @Test
    fun `enabling reminders persists settings and schedules work`() = runTest {
        val settings = ReminderSettings(isEnabled = true, snoozeDuration = Duration.ofHours(2))

        UpdateReminderSettingsUseCase(repository, scheduler)(settings)

        assertEquals(settings, repository.reminders.value)
        assertEquals(1, scheduler.schedules)
    }

    @Test
    fun `disabling reminders cancels daily and snooze work`() = runTest {
        val settings = ReminderSettings(isEnabled = false)

        UpdateReminderSettingsUseCase(repository, scheduler)(settings)

        assertEquals(settings, repository.reminders.value)
        assertEquals(1, scheduler.dailyCancellations)
        assertEquals(1, scheduler.snoozeCancellations)
    }

    @Test
    fun `onboarding completion stores a positive version`() = runTest {
        CompleteOnboardingUseCase(repository)(2)

        assertEquals(2, repository.onboarding.value)
    }
}

private class FakeSettingsRepository : SettingsRepository {
    val reminders = MutableStateFlow(ReminderSettings())
    val onboarding = MutableStateFlow(0)
    val tips = MutableStateFlow(TipProgress())

    override fun observeReminderSettings(): Flow<ReminderSettings> = reminders

    override suspend fun updateReminderSettings(settings: ReminderSettings) {
        reminders.value = settings
    }

    override fun observeOnboardingVersion(): Flow<Int> = onboarding

    override suspend fun setOnboardingVersion(version: Int) {
        onboarding.value = version
    }

    override fun observeTipProgress(): Flow<TipProgress> = tips

    override suspend fun updateTipProgress(progress: TipProgress) {
        tips.value = progress
    }
}

private class SettingsReminderScheduler : ReminderScheduler {
    var schedules = 0
    var dailyCancellations = 0
    var snoozeCancellations = 0

    override suspend fun scheduleNextReminder() {
        schedules++
    }

    override suspend fun cancelDailyReminder() {
        dailyCancellations++
    }

    override suspend fun scheduleSnooze(duration: Duration) = Unit

    override suspend fun cancelSnooze() {
        snoozeCancellations++
    }
}
