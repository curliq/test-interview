package com.john.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.john.data.accounts.DefaultAccountsRepository
import com.john.data.accounts.api.AccountsDataSource
import com.john.data.jars.DefaultJarsRepository
import com.john.data.jars.api.SavingsDataSource
import com.john.data.transactions.DefaultTransactionsRepository
import com.john.data.transactions.api.TransactionsDataSource
import com.john.domain.DefaultTransactionsService
import java.time.Clock

class HomeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val accRepo = DefaultAccountsRepository(AccountsDataSource())
            return HomeViewModel(
                DefaultTransactionsService(
                    DefaultTransactionsRepository(accRepo, TransactionsDataSource()),
                    DefaultJarsRepository(SavingsDataSource(), accRepo),
                    Clock.systemDefaultZone()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
