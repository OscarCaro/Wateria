package com.wateria.data.migration

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class LegacyPreferencesSnapshot(val values: Map<String, Any?>) {
    operator fun get(key: String): Any? = values[key]

    fun contains(key: String): Boolean = values.containsKey(key)
}

fun interface LegacyPreferencesSource {
    fun read(): LegacyPreferencesSnapshot
}

@Singleton
class AndroidLegacyPreferencesSource
@Inject
constructor(@ApplicationContext context: Context) :
    LegacyPreferencesSource {
    private val preferences =
        context.getSharedPreferences(
            "${context.packageName}_preferences",
            Context.MODE_PRIVATE
        )

    override fun read(): LegacyPreferencesSnapshot =
        LegacyPreferencesSnapshot(preferences.all.toMap())
}

internal object LegacyPreferenceKeys {
    const val PLANT_LIST = "plantlistkey"
    const val NOTIFICATIONS_ENABLED = "notif_enabled"
    const val NOTIFICATION_HOUR = "notif_hour"
    const val NOTIFICATION_MINUTE = "notif_minute"
    const val NOTIFICATION_REPETITION_HOURS = "notif_repetition"
    const val FIRST_TIME = "first_time"
    const val TIP_INDEX = "tip_idx"
    const val LAST_DAY_OF_YEAR = "last_day"
}
