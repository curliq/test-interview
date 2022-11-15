package com.john.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.john.data.transactions.Transaction as DataTransaction
import com.john.domain.TransactionsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Currency
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow

class HomeViewModel(
    private val service: TransactionsService,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
    private val mainDispatcher: CoroutineContext = Dispatchers.Main
) : ViewModel() {

    companion object {
        private const val ROUNDUP_DAYS = 7
    }

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage
    private val _roundUpTotal = MutableLiveData<String>()
    val roundUpTotal: LiveData<String> = _roundUpTotal
    private val _roundupNotice = MutableLiveData<String>()
    val roundupNotice: LiveData<String> = _roundupNotice

    init {
        viewModelScope.launch(ioDispatcher) {
            service.transactions
                .catch {
                    _errorMessage.value = "Unable to find transactions :("
                    it.printStackTrace()
                }
                .collect { txList ->
                    val data = txList.map { tx ->
                        tx.toDisplayTransaction()
                    }
                    updateRoundUpTotal()
                    withContext(mainDispatcher) {
                        if (data.isEmpty()) {
                            _errorMessage.value = "You have no transactions"
                        } else {
                            _errorMessage.value = null
                            _transactions.postValue(data)
                        }
                    }
                }
        }
    }

    fun roundTransactions() {
        GlobalScope.launch(ioDispatcher) {
            val roundup = service.performRoundUp(ROUNDUP_DAYS)
            if (roundup) {
                withContext(mainDispatcher) {
                    _roundupNotice.value = "round up completed successfully"
                }
            }
        }
    }

    private fun DataTransaction.toDisplayTransaction(): Transaction {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        val currency = Currency.getInstance(this.currencyCode)
        format.currency = currency
        val amountWithDecimals = this.amount / (10.toDouble().pow(currency.defaultFractionDigits))
        return Transaction(format.format(amountWithDecimals), this.merchant)
    }

    private fun updateRoundUpTotal() {
        viewModelScope.launch(ioDispatcher) {
            val roundups = service.roundUpTransactions(ROUNDUP_DAYS)
            if (roundups.isNotEmpty()) {
                // Just assume all roundups have the same currency for simplicity, a possible solution would be
                // grouping roundups by currency and displaying them separately
                val currency = Currency.getInstance(roundups.first().currencyCode)
                val format: NumberFormat = NumberFormat.getCurrencyInstance()
                format.currency = currency
                val totalRoundUp =
                    roundups.sumOf { it.amount } / (10.toDouble().pow(currency.defaultFractionDigits))
                withContext(mainDispatcher) {
                    _roundUpTotal.value = "Round up ${format.format(totalRoundUp)}"
                }
            }
        }
    }
}