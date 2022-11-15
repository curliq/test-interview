package com.john.data.accounts.api

import com.john.data.HttpClient
import retrofit2.Response
import retrofit2.http.GET

class AccountsDataSource {
    suspend fun getAccounts(): Accounts? {
        val client = HttpClient.getInstance().create(AccountsApi::class.java)
        return client.getAccount().body()
    }
}

interface AccountsApi {
    @GET("/api/v2/accounts")
    suspend fun getAccount(): Response<Accounts>
}
