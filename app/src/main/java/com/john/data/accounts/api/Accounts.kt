package com.john.data.accounts.api

import com.google.gson.annotations.SerializedName


data class Accounts(
    @SerializedName("accounts") val accounts: List<Account>
) {
    data class Account(
        @SerializedName("accountUid") val id: String?,
        @SerializedName("accountType") val type: String?,
        @SerializedName("defaultCategory") val defaultCategory: String?,
        @SerializedName("currency") val currency: String?,
        @SerializedName("createdAt") val createdAt: String?,
        @SerializedName("name") val name: String?,
    )
}