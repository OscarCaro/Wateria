package com.wateria.domain.model

@JvmInline
value class WateringInterval private constructor(val days: Int) {
    companion object {
        const val MIN_DAYS = 1
        const val MAX_DAYS = 40

        fun fromDays(days: Int): WateringInterval {
            require(days in MIN_DAYS..MAX_DAYS) {
                "Watering interval must be between $MIN_DAYS and $MAX_DAYS days"
            }
            return WateringInterval(days)
        }

        fun clamp(days: Int): WateringInterval = WateringInterval(days.coerceIn(MIN_DAYS, MAX_DAYS))
    }
}
