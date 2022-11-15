package com.john.data.jars.api

import com.google.gson.annotations.SerializedName

data class AddMoney(@SerializedName("success") val success: Boolean)

data class AddMoneyBody(@SerializedName("amount") val amount: Amount) {
    data class Amount(
        @SerializedName("minorUnits") val amount: Int,
        @SerializedName("currency") val currency: String
    )
}