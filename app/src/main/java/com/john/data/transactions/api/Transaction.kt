package com.john.data.transactions.api

import com.google.gson.annotations.SerializedName

data class Transactions(@SerializedName("feedItems") var transactions: List<Transaction>? = null) {
    data class Transaction(
        @SerializedName("feedItemUid") var feedItemUid: String? = null,
        @SerializedName("categoryUid") var categoryUid: String? = null,
        @SerializedName("amount") var amount: Amount? = Amount(),
        @SerializedName("sourceAmount") var sourceAmount: SourceAmount? = SourceAmount(),
        @SerializedName("direction") var direction: String? = null,
        @SerializedName("updatedAt") var updatedAt: String? = null,
        @SerializedName("transactionTime") var transactionTime: String? = null,
        @SerializedName("source") var source: String? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("transactingApplicationUserUid") var transactingApplicationUserUid: String? = null,
        @SerializedName("counterPartyType") var counterPartyType: String? = null,
        @SerializedName("counterPartyUid") var counterPartyUid: String? = null,
        @SerializedName("counterPartyName") var counterPartyName: String? = null,
        @SerializedName("counterPartySubEntityUid") var counterPartySubEntityUid: String? = null,
        @SerializedName("counterPartySubEntityName") var counterPartySubEntityName: String? = null,
        @SerializedName("counterPartySubEntityIdentifier") var counterPartySubEntityIdentifier: String? = null,
        @SerializedName("counterPartySubEntitySubIdentifier") var counterPartySubEntitySubIdentifier: String? = null,
        @SerializedName("reference") var reference: String? = null,
        @SerializedName("country") var country: String? = null,
        @SerializedName("spendingCategory") var spendingCategory: String? = null,
        @SerializedName("hasAttachment") var hasAttachment: Boolean? = null,
        @SerializedName("hasReceipt") var hasReceipt: Boolean? = null,
        @SerializedName("batchPaymentDetails") var batchPaymentDetails: String? = null

    ) {
        data class Amount(
            @SerializedName("currency") var currency: String? = null,
            @SerializedName("minorUnits") var minorUnits: Int? = null
        )

        data class SourceAmount(
            @SerializedName("currency") var currency: String? = null,
            @SerializedName("minorUnits") var minorUnits: Int? = null
        )
    }
}
