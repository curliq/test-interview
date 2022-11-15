package com.john.data.transactions

import java.time.LocalDateTime

data class Transaction(
    val id: String,
    val amount: Long,
    val currencyCode: String,
    val merchant: String,
    val dateTime: LocalDateTime
)
