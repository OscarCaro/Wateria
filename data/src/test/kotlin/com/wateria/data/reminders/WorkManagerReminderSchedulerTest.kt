package com.wateria.data.reminders

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.wateria.data.preferences.WateriaPreferencesDataSource
import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.time.TimeProvider
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class WorkManagerReminderSchedulerTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var preferences: WateriaPreferencesDataSource
    private lateinit var scheduler: WorkManagerReminderScheduler
    private lateinit var dataStoreScope: CoroutineScope

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        workManager = WorkManager.getInstance(context)
        dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        preferences =
            WateriaPreferencesDataSource(
                PreferenceDataStoreFactory.create(
                    scope = dataStoreScope,
                    produceFile = {
                        File(temporaryFolder.newFolder(), "scheduler.preferences_pb")
                    }
                )
            )
        scheduler =
            WorkManagerReminderScheduler(
                workManager = workManager,
                preferences = preferences,
                timeProvider = SchedulerTimeProvider
            )
    }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
        WorkManagerTestInitHelper.closeWorkDatabase()
    }

    @Test
    fun `daily reminders are unique replaceable and tagged`() = runTest {
        preferences.updateReminderSettings(
            ReminderSettings(isEnabled = true, time = LocalTime.of(18, 0))
        )

        scheduler.scheduleNextReminder()
        scheduler.scheduleNextReminder()

        val work =
            workManager
                .getWorkInfosForUniqueWork(WorkManagerReminderScheduler.DAILY_REMINDER_WORK)
                .get()
        val active = work.filter { info -> !info.state.isFinished }
        assertEquals(1, active.size)
        assertTrue(WorkManagerReminderScheduler.DAILY_REMINDER_TAG in active.single().tags)
        assertEquals(WorkInfo.State.ENQUEUED, active.single().state)
    }

    @Test
    fun `disabled reminders cancel active daily work`() = runTest {
        preferences.updateReminderSettings(ReminderSettings(isEnabled = true))
        scheduler.scheduleNextReminder()
        preferences.updateReminderSettings(ReminderSettings(isEnabled = false))

        scheduler.scheduleNextReminder()

        val work =
            workManager
                .getWorkInfosForUniqueWork(WorkManagerReminderScheduler.DAILY_REMINDER_WORK)
                .get()
        assertTrue(work.all { info -> info.state.isFinished })
    }

    @Test
    fun `snooze work uses its own unique stream`() = runTest {
        scheduler.scheduleSnooze(Duration.ofHours(2))

        val work =
            workManager
                .getWorkInfosForUniqueWork(WorkManagerReminderScheduler.SNOOZE_REMINDER_WORK)
                .get()
                .single()
        assertEquals(WorkInfo.State.ENQUEUED, work.state)
        assertTrue(WorkManagerReminderScheduler.SNOOZE_REMINDER_TAG in work.tags)
    }
}

private object SchedulerTimeProvider : TimeProvider {
    private val now = Instant.parse("2026-07-17T10:00:00Z")

    override fun instant(): Instant = now

    override fun today(): LocalDate = LocalDate.ofInstant(now, ZoneOffset.UTC)

    override fun zoneId(): ZoneId = ZoneOffset.UTC
}
