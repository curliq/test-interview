package com.john.data.jars.api

import com.john.data.HttpClient
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

class SavingsDataSource {

    // TODO: inject http client

    suspend fun getGoals(accountId: String): Goals? {
        val client = HttpClient.getInstance().create(SavingsApi::class.java)
        return client.getGoals(accountId).body()
    }

    suspend fun createGoal(accountId: String, name: String, currency: String): CreateGoal? {
        val client = HttpClient.getInstance().create(SavingsApi::class.java)
        return client.createGoal(accountId, CreateGoalBody(name, currency)).body()
    }

    suspend fun deposit(accountId: String, goalId: String, amount: Int, currency: String): AddMoney? {
        val client = HttpClient.getInstance().create(SavingsApi::class.java)
        return client.addMoney(
            accountId,
            goalId,
            UUID.randomUUID().toString(),
            AddMoneyBody(AddMoneyBody.Amount(amount, currency))
        ).body()
    }
}

interface SavingsApi {
    @GET("/api/v2/account/{accountUid}/savings-goals")
    suspend fun getGoals(@Path("accountUid") account: String): Response<Goals>

    @PUT("/api/v2/account/{accountUid}/savings-goals")
    suspend fun createGoal(
        @Path("accountUid") account: String,
        @Body body: CreateGoalBody
    ): Response<CreateGoal>

    @PUT("/api/v2/account/{accountUid}/savings-goals/{savingsGoalUid}/add-money/{transferUid}")
    suspend fun addMoney(
        @Path("accountUid") account: String,
        @Path("savingsGoalUid") savingsGoalUid: String,
        @Path("transferUid") transferUid: String,
        @Body body: AddMoneyBody
    ): Response<AddMoney>
}
