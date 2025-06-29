package com.booji.foundryconnect.data.network

import retrofit2.http.Body
import retrofit2.http.POST

/** Retrofit API interface for communicating with Azure Foundry. */
interface FoundryApiService {

    /**
     * Retrofit interface for Azure Foundry chat completions.
     *
     * We return a Retrofit \[Response\]<FoundryResponse> rather than
     * just FoundryResponse so that the repository can:
     *  1. Inspect HTTP status codes directly.
     *  2. Read raw error bodies on non-2xx responses.
     *  3. Differentiate network/server errors from JSON parsing issues.
     */
    @POST("chat/completions")
    suspend fun sendMessage(
        @Body request: FoundryRequest
    ): retrofit2.Response<FoundryResponse>
}
