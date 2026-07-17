package com.wateria.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Plant(
    val id: PlantId,
    val name: String,
    val icon: PlantIcon,
    val wateringInterval: WateringInterval,
    val nextWateringDate: LocalDate
)

sealed interface WateringStatus {
    data class Upcoming(val daysRemaining: Int) : WateringStatus

    data object DueToday : WateringStatus

    data class Overdue(val daysOverdue: Int) : WateringStatus
}

fun Plant.wateringStatus(today: LocalDate): WateringStatus {
    val days = ChronoUnit.DAYS.between(today, nextWateringDate).toInt()
    return when {
        days > 0 -> WateringStatus.Upcoming(days)
        days == 0 -> WateringStatus.DueToday
        else -> WateringStatus.Overdue(-days)
    }
}

val PlantOrder: Comparator<Plant> =
    compareBy<Plant> { it.nextWateringDate }
        .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        .thenBy { it.id.value }
