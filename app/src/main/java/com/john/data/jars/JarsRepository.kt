package com.john.data.jars

import com.john.data.accounts.AccountsRepository
import com.john.data.jars.api.Goals
import com.john.data.jars.api.SavingsDataSource
import kotlinx.coroutines.flow.firstOrNull

/**
 * Jars as in saving jars, aka "Goals"
 */
interface JarsRepository {
    suspend fun getOrCreateJar(name: String): Jar?

    /**
     * @param transactions transactions contained in this deposit for history storage purposes
     */
    suspend fun moveMoneyToJar(
        jarId: String,
        amount: Long,
        transactions: List<MoneyMove>,
    ): Boolean

    data class MoneyMove(val amount: Long, val currency: String)
}

class DefaultJarsRepository(
    private val dataSource: SavingsDataSource,
    private val accountsRepository: AccountsRepository
) :
    JarsRepository {

    private val cache = emptyList<Jar>()

    companion object {
        const val SAVINGS_JAR = "savings"
    }

    private fun Goals?.mapGoals(): List<Jar> {
        return this?.goals?.map {
            Jar(it.id ?: "", it.name ?: "", it.target?.amount ?: 0, it.target?.currency ?: "")
        } ?: emptyList()
    }

    override suspend fun getOrCreateJar(name: String): Jar? {
        val currency = "GBP"
        return cache.firstOrNull { it.name == SAVINGS_JAR }
            ?: dataSource.getGoals(getAccountId()).mapGoals().firstOrNull { it.name == name }
            ?: dataSource.createGoal(getAccountId(), name, currency)?.run { Jar(this.id ?: "", name, 0, currency) }
    }

    private suspend fun getAccountId(): String {
        // TODO: Handle account ID not returned
        return accountsRepository.account.firstOrNull()?.id ?: ""
    }

    override suspend fun moveMoneyToJar(
        jarId: String,
        amount: Long,
        transactions: List<JarsRepository.MoneyMove>
    ): Boolean {
        return dataSource.deposit(getAccountId(), jarId, amount.toInt(), "GBP")?.success ?: false
    }

}
