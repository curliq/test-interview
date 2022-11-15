package com.john.data.jars.api

import com.google.gson.annotations.SerializedName

data class CreateGoal(
    @SerializedName("savingsGoalUid") val id: String?
)

data class CreateGoalBody(
    @SerializedName("name") val name: String,
    @SerializedName("currency") val currency: String
)
