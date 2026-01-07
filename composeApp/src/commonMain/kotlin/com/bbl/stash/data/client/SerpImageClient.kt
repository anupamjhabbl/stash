package com.bbl.stash.data.client

import com.bbl.stash.data.client.model.SerpApiResult
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface SerpImageClient {
    @GET("search.json")
    suspend fun getImageUri(@Query("engine") engine: String, @Query("q") query: String, @Query("api_key") apiKey: String): SerpApiResult
}