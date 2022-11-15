package com.john.data.transactions.api

import com.john.data.HttpClient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class TransactionsDataSource {
    /**
     * TODO: Error handling: catch each potential http problem
     * TODO: inject http client
     */
    suspend fun getTransactions(account: String, category: String, changesSince: String): Transactions? {
        val client = HttpClient.getInstance().create(TransactionsApi::class.java)
        return client.getTransactions(account, category, changesSince).body()
    }

}

interface TransactionsApi {
    @GET("/api/v2/feed/account/{accountUid}/category/{categoryUid}")
    suspend fun getTransactions(
        @Path("accountUid") account: String,
        @Path("categoryUid") category: String,
        @Query("changesSince") changesSince:String
    ): Response<Transactions>
}
