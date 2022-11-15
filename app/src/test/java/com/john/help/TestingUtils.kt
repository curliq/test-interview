package com.john.help

import com.john.data.transactions.Transaction
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

object TestingUtils {

    /**
     * Mock `LocalDateTime.now()`
     */
    fun mockNow(fakeNow: LocalDateTime, clock: Clock) {
        val fixedClock = Clock.fixed(fakeNow.toInstant(ZoneOffset.UTC), ZoneId.systemDefault())
        whenever(clock.instant()).thenReturn(fixedClock.instant())
        whenever(clock.zone).thenReturn(fixedClock.zone)
    }

    fun makeTx(amount: Long): Transaction {
        return Transaction("1", amount, "EUR", "Gucci", LocalDateTime.now())
    }

    fun makeTx(currency: String = "EUR"): Transaction {
        return Transaction("1", 200, currency, "Gucci", LocalDateTime.now())
    }

}
