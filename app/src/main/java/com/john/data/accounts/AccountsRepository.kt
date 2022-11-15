package com.john.data.accounts

import com.john.data.accounts.api.Accounts
import com.john.data.accounts.api.AccountsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface AccountsRepository {
    val account: Flow<Accounts.Account?>
}

class DefaultAccountsRepository(private val accountsApi: AccountsDataSource) : AccountsRepository {

    private val cache: Accounts.Account? = null

    override val account: Flow<Accounts.Account?>
        get() = flow {
            emit(cache ?: accountsApi.getAccounts()?.accounts?.firstOrNull())
        }.catch { it.printStackTrace() }

}
