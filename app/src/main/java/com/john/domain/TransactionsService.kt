package com.john.domain

import com.john.data.transactions.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionsService {
    val transactions: Flow<List<Transaction>>

    /**
     * Round up all the transactions in the given time period and save them in the savings jar (aka goal)
     *
     * @return success
     */
    suspend fun performRoundUp(daysUntilNow: Int): Boolean

    /**
     * Preview roundUps
     */
    suspend fun roundUpTransactions(daysUntilNow: Int): List<RoundUp>

    /**
     * Object to represent the round up amount of a given transaction
     */
    data class RoundUp(val amount: Long, val transactionId: String, val currencyCode: String)
}
