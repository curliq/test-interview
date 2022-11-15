package com.john.data.transactions

import com.john.data.transactions.api.TransactionsDataSource
import com.john.data.accounts.AccountsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.OffsetDateTime

interface TransactionsRepository {
    fun getTransactions(): Flow<List<Transaction>?>
    suspend fun getTransactionsPeriod(from: LocalDateTime, to: LocalDateTime): List<Transaction>
}

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultTransactionsRepository(
    private val accountsRepository: AccountsRepository,
    private val transactionsDataSource: TransactionsDataSource
) : TransactionsRepository {

    private val transactions: StateFlow<List<Transaction>?> by lazy {
        accountsRepository.account.filterNotNull().flatMapLatest { account ->
            flow {
                if (account.id == null || account.defaultCategory == null) {
                    emit(null)
                } else {
                    // TODO Implement pagination
                    val since = OffsetDateTime.now().minusMonths(1).toString()
                    val transactions =
                        transactionsDataSource.getTransactions(
                            account.id,
                            account.defaultCategory,
                            since
                        )?.transactions?.map {
                            // TODO: Move this logic to a mapper outside of the repo and handle nullable backups logic
                            Transaction(
                                it.feedItemUid ?: "",
                                (it.amount?.minorUnits ?: 0).toLong(),
                                it.amount?.currency ?: "",
                                it.counterPartyName ?: "",
                                OffsetDateTime.parse(it.transactionTime ?: "").toLocalDateTime()
                            )
                        }
                    if (transactions == null) {
                        throw Exception("Unable to get transactions")
                    } else {
                        emit(transactions)
                    }
                }
            }
        }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())
    }

    /**
     * A null list is considered an error state
     * TODO: Return something like a `Result` sealed class with an Error enum for actual error handling
     *
     * This ^ is a StateFlow for a quick solution to caching
     * A better caching solution would be storing the transactions PER category/account pair in a database,
     * or at least a local variable (a MutableMap probably).
     *
     * Currently if you change category/account this will return the same cached data which will be the wrong data
     *
     * I also don't think a StateFlow is optimal here because it requires an initial state,
     * and `null` is used for errors, an emptyList means no results, and a new "Loading" state wouldn't be ideal because
     * that's UI related
     */
    override fun getTransactions(): Flow<List<Transaction>?> {
        return transactions
    }

    /**
     * This method is responsible for returning transaction given a date query,
     * so it potentially needs an extra DataSource call.
     *
     * For cases when the date is within the cached data than the filter can be
     * applied here directly after some cache negotiation (i.e. verifying cache is up to date) (TODO)
     */
    override suspend fun getTransactionsPeriod(from: LocalDateTime, to: LocalDateTime): List<Transaction> {
        return transactions.value ?: emptyList()
    }
}