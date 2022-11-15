package com.john.domain

import com.john.data.jars.DefaultJarsRepository
import com.john.data.jars.JarsRepository
import com.john.data.transactions.Transaction
import com.john.data.transactions.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.LocalDateTime
import java.util.Currency
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.pow

class DefaultTransactionsService(
    private val txRepo: TransactionsRepository,
    private val jarsRepo: JarsRepository,
    private val clock: Clock
) : TransactionsService {

    override val transactions: Flow<List<Transaction>> = txRepo.getTransactions().filterNotNull()

    override suspend fun performRoundUp(daysUntilNow: Int): Boolean {
        val jar = jarsRepo.getOrCreateJar(DefaultJarsRepository.SAVINGS_JAR) ?: return false
        val transactions = transactionsToRoundUps(daysUntilNow)
        val amount = transactions.sumOf { it.amount }
        if (amount > 0) {
            return jarsRepo.moveMoneyToJar(
                jar.id,
                amount,
                transactions.map { JarsRepository.MoneyMove(it.amount, it.currencyCode) }
            )
        }
        return false
    }

    override suspend fun roundUpTransactions(daysUntilNow: Int): List<TransactionsService.RoundUp> {
        return transactionsToRoundUps(daysUntilNow)
    }

    private suspend fun transactionsToRoundUps(daysUntilNow: Int): List<TransactionsService.RoundUp> {
        val now = LocalDateTime.now(clock)
        val from = now.minusDays(daysUntilNow.toLong()).withHour(0).withMinute(0).withSecond(0).withNano(0)
        val transactions = txRepo.getTransactionsPeriod(from, now)
        return transactions.map { tx ->
            TransactionsService.RoundUp(calculateRoundUp(tx), tx.id, tx.currencyCode)
        }
    }

    /**
     * Calculate the amount round up, eg 120 returns 80 (ie $0.80)
     * 1. Reduce number to float with correct decimal cases given the currency
     * 2. Round up number and find difference
     * 3. Round up difference to max 5th decimal case
     *    (there's probably a more reliable industry standard way of doing this than using BigDecimal)
     * 4. Convert difference back to whole number
     */
    private fun calculateRoundUp(transaction: Transaction): Long {
        val currency = Currency.getInstance(transaction.currencyCode)
        val amountWithDecimals = transaction.amount / (10.toDouble().pow(currency.defaultFractionDigits))
        val roundUp = ceil(amountWithDecimals) - amountWithDecimals
        val roundFloat = BigDecimal(roundUp.toString()).setScale(5, RoundingMode.HALF_UP)
        return (roundFloat.toDouble() * (10.toDouble().pow(currency.defaultFractionDigits))).toLong()
    }

}