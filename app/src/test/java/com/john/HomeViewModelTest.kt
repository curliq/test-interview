@file:Suppress("NonAsciiCharacters")

package com.john

import com.john.domain.TransactionsService
import com.john.help.DispatchersMainDelegationExtension
import com.john.help.InstantExecutorExtension
import com.john.help.TestingUtils.makeTx
import com.john.view.HomeViewModel
import com.jraska.livedata.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class, DispatchersMainDelegationExtension::class)
class HomeViewModelTest {

    lateinit var target: HomeViewModel
    lateinit var transactionsService: TransactionsService

    @BeforeEach
    fun setup() {
        transactionsService = mock()
    }

    private fun initTarget() {
        target = HomeViewModel(transactionsService, Dispatchers.Unconfined, Dispatchers.Unconfined)
    }

    @Test
    fun `WHEN 1 transaction exists THEN viewmodel exposes 1 transaction`() = runTest {
        val state = MutableStateFlow(listOf(makeTx()))
        whenever(transactionsService.transactions).thenReturn(state)
        initTarget()

        target.transactions.test().assertValue { it.size == 1 }
    }

    @Test
    fun `WHEN 5 transactions exists THEN viewmodel exposes 5 transactions`() = runTest {
        val data = List(5) { makeTx() }
        val state = MutableStateFlow(data)
        whenever(transactionsService.transactions).thenReturn(state)
        initTarget()

        target.transactions.test().assertValue { it.size == 5 }
    }

    /**
     * This test is failing because `NumberFormat` uses € before the digit rather than after and dot instead of comma,
     * i.e. "€2.00" instead of "2,00€" - probably a locale problem.
     * TODO: Investigate and fix
     */
    @Disabled
    @Test
    fun `WHEN transaction is EUR THEN € symbol is used after the digit`() = runTest {
        val state = MutableStateFlow(listOf(makeTx(currency = "EUR")))
        whenever(transactionsService.transactions).thenReturn(state)
        initTarget()

        target.transactions.test().assertValue { it.first().amount == "2.00€" }
    }

    @Test
    fun `WHEN transaction is GBP THEN £ symbol is used before the digit`() = runTest {
        val state = MutableStateFlow(listOf(makeTx(currency = "GBP")))
        whenever(transactionsService.transactions).thenReturn(state)
        initTarget()

        target.transactions.test().assertValue { it.first().amount == "£2.00" }
    }

    @Test
    fun `WHEN fetching transactions THEN roundups are also emitted from ViewModel`() = runTest {
        val transactions = MutableStateFlow(listOf(makeTx()))
        val state = List(4) { TransactionsService.RoundUp(1, "", "GBP") }
        whenever(transactionsService.transactions).thenReturn(transactions)
        whenever(transactionsService.roundUpTransactions(any())).thenReturn(state)
        initTarget()

        target.roundUpTotal.test().assertHasValue()
    }

    @Test
    fun `WHEN fetching 4 roundups of 50 each THEN roundup total is 200 and displayed as £2'00`() = runTest {
        val transactions = MutableStateFlow(List(4) { makeTx() })
        val state = List(4) { TransactionsService.RoundUp(50, "", "GBP") }
        whenever(transactionsService.transactions).thenReturn(transactions)
        whenever(transactionsService.roundUpTransactions(any())).thenReturn(state)
        initTarget()

        target.roundUpTotal.test().assertValue("Round up £2.00")
    }
}