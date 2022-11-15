package com.john

import com.john.help.TestingUtils.makeTx
import com.john.data.jars.DefaultJarsRepository.Companion.SAVINGS_JAR
import com.john.data.jars.Jar
import com.john.data.jars.JarsRepository
import com.john.data.transactions.Transaction
import com.john.data.transactions.TransactionsRepository
import com.john.domain.DefaultTransactionsService
import com.john.domain.TransactionsService
import com.john.help.TestingUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsServiceTest {

    lateinit var target: TransactionsService
    lateinit var transactionsRepository: TransactionsRepository
    lateinit var jarsRepository: JarsRepository
    lateinit var clock: Clock

    @BeforeEach
    fun setup() {
        transactionsRepository = mock {
            on { getTransactions() } doReturn flowOf(emptyList())
        }
        jarsRepository = mock()
        clock = mock()
        target = DefaultTransactionsService(transactionsRepository, jarsRepository, clock)
        TestingUtils.mockNow(LocalDateTime.now(), clock)
    }

    @Test
    fun `WHEN subscribe to transactions THEN repository is fetched`() = runTest {
        target.transactions.collect()
        verify(transactionsRepository, times(1)).getTransactions()
    }

    @Test
    fun `WHEN rounding up past 3 days THEN only and all transactions withing past 3 days are selected`() = runTest {
        TestingUtils.mockNow(LocalDateTime.of(2022, 11, 10, 16, 30, 15), clock)
        whenever(transactionsRepository.getTransactionsPeriod(any(), any())).thenReturn(emptyList())
        whenever(jarsRepository.getOrCreateJar(any())).thenReturn(Jar("id", "", 0, ""))
        target.performRoundUp(3)
        verify(transactionsRepository, times(1)).getTransactionsPeriod(
            LocalDateTime.of(2022, 11, 7, 0, 0, 0),
            LocalDateTime.of(2022, 11, 10, 16, 30, 15)
        )
    }

    @Test
    fun `WHEN rounding up 1 transaction of 140 THEN round up of 60 is added to a jar`() = runTest {
        val state = listOf(makeTx(140))
        whenever(transactionsRepository.getTransactionsPeriod(any(), any())).thenReturn(state)
        whenever(jarsRepository.getOrCreateJar(any())).thenReturn(Jar("id", "", 0, ""))
        target.performRoundUp(0)

        val roundUp = 60L
        verify(jarsRepository, times(1)).getOrCreateJar(SAVINGS_JAR)
        verify(jarsRepository, times(1)).moveMoneyToJar(any(), eq(roundUp), any())
    }

    @Test
    fun `WHEN rounding up 3 transaction THEN round up sum are added to a jar`() = runTest {
        val state = listOf(makeTx(435), makeTx(520), makeTx(87))
        whenever(transactionsRepository.getTransactionsPeriod(any(), any())).thenReturn(state)
        whenever(jarsRepository.getOrCreateJar(any())).thenReturn(Jar("id", "", 0, ""))
        target.performRoundUp(0)

        val roundUp = 158L
        // 0.65 + 0.80 + 0.13 = £1.58
        verify(jarsRepository, times(1)).getOrCreateJar(SAVINGS_JAR)
        verify(jarsRepository, times(1)).moveMoneyToJar(any(), eq(roundUp), any())
    }

    @Test
    fun `WHEN rounding up a transaction of 15000 THEN then no roundUp is added`() = runTest {
        val state = listOf(makeTx(15000))
        whenever(transactionsRepository.getTransactionsPeriod(any(), any())).thenReturn(state)
        whenever(jarsRepository.getOrCreateJar(any())).thenReturn(Jar("id", "", 0, ""))
        target.performRoundUp(0)

        verify(jarsRepository, times(1)).getOrCreateJar(SAVINGS_JAR)
        verify(jarsRepository, times(0)).moveMoneyToJar(any(), any(), any())
    }

    @Test
    fun `WHEN rounding up a transaction of 15070 THEN then only 30 roundUp is added`() = runTest {
        val state = listOf(makeTx(15070))
        whenever(transactionsRepository.getTransactionsPeriod(any(), any())).thenReturn(state)
        whenever(jarsRepository.getOrCreateJar(any())).thenReturn(Jar("id", "", 0, ""))
        target.performRoundUp(0)

        verify(jarsRepository, times(1)).getOrCreateJar(SAVINGS_JAR)
        verify(jarsRepository, times(1)).moveMoneyToJar(any(), eq(30), any())
    }
}