package com.wateria.domain.time

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

interface TimeProvider {
    fun instant(): Instant

    fun today(): LocalDate

    fun zoneId(): ZoneId
}

class ClockTimeProvider(private val clock: Clock) : TimeProvider {
    override fun instant(): Instant = clock.instant()

    override fun today(): LocalDate = LocalDate.now(clock)

    override fun zoneId(): ZoneId = clock.zone
}
