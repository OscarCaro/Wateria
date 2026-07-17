@file:Suppress("MagicNumber")

package com.wateria.data.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.wateria.data.R
import com.wateria.domain.usecase.DuePlant
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface ReminderNotificationPublisher {
    fun show(duePlants: List<DuePlant>)

    fun cancel()
}

object ReminderNotificationContract {
    const val ACTION_WATER = "com.wateria.action.WATER_PLANT"
    const val ACTION_SNOOZE = "com.wateria.action.SNOOZE_REMINDER"
    const val EXTRA_PLANT_ID = "plant_id"
    const val NOTIFICATION_ID = 1
    const val CHANNEL_ID = "watering_reminders"
}

@Singleton
class AndroidReminderNotificationPublisher
@Inject
constructor(
    @param:ApplicationContext private val context: Context
) : ReminderNotificationPublisher {
    override fun show(duePlants: List<DuePlant>) {
        if (duePlants.isEmpty() || !canNotify()) return
        createChannel()
        val builder =
            NotificationCompat.Builder(context, ReminderNotificationContract.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(WATERIA_GREEN)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(openAppPendingIntent())
                .addAction(
                    R.drawable.ic_notification,
                    context.getString(R.string.reminder_action_snooze),
                    snoozePendingIntent()
                )

        if (duePlants.size == 1) {
            val plant = duePlants.single().plant
            builder
                .setContentTitle(plant.name)
                .setContentText(context.getString(R.string.reminder_single_body))
                .addAction(
                    R.drawable.ic_notification,
                    context.getString(R.string.reminder_action_water),
                    waterPendingIntent(plant.id.value)
                )
        } else {
            builder
                .setContentTitle(context.getString(R.string.reminder_multiple_title))
                .setContentText(
                    context.resources.getQuantityString(
                        R.plurals.reminder_multiple_body,
                        duePlants.size,
                        duePlants.size
                    )
                )
                .setNumber(duePlants.size)
        }

        NotificationManagerCompat.from(context).notify(
            ReminderNotificationContract.NOTIFICATION_ID,
            builder.build()
        )
    }

    override fun cancel() {
        NotificationManagerCompat.from(context).cancel(ReminderNotificationContract.NOTIFICATION_ID)
    }

    private fun canNotify(): Boolean {
        val hasPermission =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
        return hasPermission && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel =
            NotificationChannel(
                ReminderNotificationContract.CHANNEL_ID,
                context.getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.reminder_channel_description)
            }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun openAppPendingIntent(): PendingIntent {
        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(context.packageName)
                ?: Intent(Intent.ACTION_MAIN).setPackage(context.packageName)
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(
            context,
            OPEN_APP_REQUEST_CODE,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun waterPendingIntent(plantId: String): PendingIntent {
        val intent =
            Intent(ReminderNotificationContract.ACTION_WATER)
                .setPackage(context.packageName)
                .putExtra(ReminderNotificationContract.EXTRA_PLANT_ID, plantId)
        return PendingIntent.getBroadcast(
            context,
            plantId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun snoozePendingIntent(): PendingIntent {
        val intent =
            Intent(ReminderNotificationContract.ACTION_SNOOZE).setPackage(context.packageName)
        return PendingIntent.getBroadcast(
            context,
            SNOOZE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private companion object {
        const val WATERIA_GREEN = 0xFF006B4F.toInt()
        const val OPEN_APP_REQUEST_CODE = 10
        const val SNOOZE_REQUEST_CODE = 11
    }
}
