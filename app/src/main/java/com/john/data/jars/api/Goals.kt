package com.john.data.jars.api

import com.google.gson.annotations.SerializedName

data class Goals(
    @SerializedName("savingsGoalList") val goals: List<SavingGoal>
) {
    data class SavingGoal(
        @SerializedName("savingsGoalUid") val id: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("target") val target: Target?
    ) {
        data class Target(
            @SerializedName("currency") val currency: String?,
            @SerializedName("minorUnits") val amount: Int?,
        )
    }
}