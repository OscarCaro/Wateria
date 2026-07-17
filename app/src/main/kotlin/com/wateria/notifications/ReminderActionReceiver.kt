package com.wateria.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wateria.data.reminders.ReminderNotificationContract
import com.wateria.domain.model.PlantId
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.usecase.ObserveReminderSettingsUseCase
import com.wateria.domain.usecase.WaterPlantUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReminderActionReceiver : BroadcastReceiver() {
    @Inject lateinit var waterPlant: WaterPlantUseCase

    @Inject lateinit var observeReminderSettings: ObserveReminderSettingsUseCase

    @Inject lateinit var scheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                runCatching {
                    when (intent.action) {
                        ReminderNotificationContract.ACTION_WATER -> water(intent)
                        ReminderNotificationContract.ACTION_SNOOZE -> snooze()
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun water(intent: Intent) {
        val rawId = intent.getStringExtra(ReminderNotificationContract.EXTRA_PLANT_ID)
        val plantId = rawId?.let(PlantId::parseOrNull) ?: return
        runCatching { waterPlant(plantId) }
        scheduler.cancelDisplayedReminder()
    }

    private suspend fun snooze() {
        val settings = observeReminderSettings().first()
        if (settings.isEnabled) scheduler.scheduleSnooze(settings.snoozeDuration)
        scheduler.cancelDisplayedReminder()
    }
}
