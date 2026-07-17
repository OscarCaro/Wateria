package com.wateria.data.reminders

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.wateria.domain.model.Plant
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.model.PlantId
import com.wateria.domain.model.ReminderSettings
import com.wateria.domain.model.TipProgress
import com.wateria.domain.model.WateringInterval
import com.wateria.domain.repository.PlantRepository
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.repository.SettingsRepository
import com.wateria.domain.time.TimeProvider
import com.wateria.domain.usecase.DuePlant
import com.wateria.domain.usecase.GetDuePlantsUseCase
import com.wateria.domain.usecase.ObserveReminderSettingsUseCase
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ReminderWorkersTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val today = LocalDate.of(2026, 7, 17)

    @Test
    fun `daily worker publishes due plants and schedules the next occurrence`() = runTest {
        val plants = WorkerPlantRepository(listOf(plant(today)))
        val settings = WorkerSettingsRepository(ReminderSettings(isEnabled = true))
        val scheduler = WorkerReminderScheduler()
        val publisher = RecordingNotificationPublisher()
        val factory =
            workerFactory(plants, settings, scheduler, publisher)
        val worker =
            TestListenableWorkerBuilder<DailyReminderWorker>(context)
                .setWorkerFactory(factory)
                .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        assertEquals(listOf(testId()), publisher.shown.single().map { it.plant.id })
        assertEquals(0, scheduler.dailySchedules)
        assertEquals(1, scheduler.afterCurrentSchedules)
    }

    @Test
    fun `disabled daily worker clears stale notification without rescheduling`() = runTest {
        val plants = WorkerPlantRepository(listOf(plant(today)))
        val settings = WorkerSettingsRepository(ReminderSettings(isEnabled = false))
        val scheduler = WorkerReminderScheduler()
        val publisher = RecordingNotificationPublisher()
        val worker =
            TestListenableWorkerBuilder<DailyReminderWorker>(context)
                .setWorkerFactory(workerFactory(plants, settings, scheduler, publisher))
                .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        assertEquals(1, publisher.cancellations)
        assertEquals(0, scheduler.dailySchedules)
        assertEquals(0, scheduler.afterCurrentSchedules)
        assertTrue(publisher.shown.isEmpty())
    }

    private fun workerFactory(
        plants: PlantRepository,
        settings: SettingsRepository,
        scheduler: ReminderScheduler,
        publisher: ReminderNotificationPublisher
    ): WorkerFactory = object : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? = when (workerClassName) {
            DailyReminderWorker::class.java.name ->
                DailyReminderWorker(
                    appContext,
                    workerParameters,
                    GetDuePlantsUseCase(plants, WorkerTimeProvider(today)),
                    ObserveReminderSettingsUseCase(settings),
                    scheduler,
                    publisher
                )

            SnoozeReminderWorker::class.java.name ->
                SnoozeReminderWorker(
                    appContext,
                    workerParameters,
                    GetDuePlantsUseCase(plants, WorkerTimeProvider(today)),
                    ObserveReminderSettingsUseCase(settings),
                    publisher
                )

            else -> null
        }
    }

    private fun plant(nextWatering: LocalDate): Plant = Plant(
        id = testId(),
        name = "Fern",
        icon = PlantIcon.fromKey("common_01"),
        wateringInterval = WateringInterval.fromDays(5),
        nextWateringDate = nextWatering
    )

    private fun testId(): PlantId =
        PlantId.from(UUID.fromString("00000000-0000-0000-0000-000000000001"))
}

private class WorkerPlantRepository(initial: List<Plant>) : PlantRepository {
    private val plants = MutableStateFlow(initial)

    override fun observePlants(): Flow<List<Plant>> = plants
    override suspend fun getPlant(id: PlantId): Plant? = plants.value.firstOrNull { it.id == id }
    override suspend fun insert(plant: Plant) {
        plants.value += plant
    }
    override suspend fun update(plant: Plant) {
        plants.value =
            plants.value.map { if (it.id == plant.id) plant else it }
    }
    override suspend fun delete(id: PlantId) {
        plants.value = plants.value.filterNot { it.id == id }
    }
    override suspend fun deleteAll() {
        plants.value = emptyList()
    }
}

private class WorkerSettingsRepository(initial: ReminderSettings) : SettingsRepository {
    private val reminders = MutableStateFlow(initial)
    private val onboarding = MutableStateFlow(0)
    private val tips = MutableStateFlow(TipProgress())

    override fun observeReminderSettings(): Flow<ReminderSettings> = reminders
    override suspend fun updateReminderSettings(settings: ReminderSettings) {
        reminders.value =
            settings
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

private class WorkerReminderScheduler : ReminderScheduler {
    var dailySchedules = 0
    var afterCurrentSchedules = 0

    override suspend fun scheduleNextReminder() {
        dailySchedules++
    }
    override suspend fun scheduleNextReminderAfterCurrent() {
        afterCurrentSchedules++
    }
    override suspend fun cancelDailyReminder() = Unit
    override suspend fun scheduleSnooze(duration: Duration) = Unit
    override suspend fun cancelSnooze() = Unit
}

private class RecordingNotificationPublisher : ReminderNotificationPublisher {
    val shown = mutableListOf<List<DuePlant>>()
    var cancellations = 0

    override fun show(duePlants: List<DuePlant>) {
        shown += duePlants
    }
    override fun cancel() {
        cancellations++
    }
}

private class WorkerTimeProvider(private val date: LocalDate) : TimeProvider {
    override fun instant(): Instant = date.atStartOfDay().toInstant(ZoneOffset.UTC)
    override fun today(): LocalDate = date
    override fun zoneId(): ZoneId = ZoneOffset.UTC
}
