package com.example.stash.data.client

import com.example.stash.data.client.model.StashCategoryBatch
import com.example.stash.data.client.model.StashItemBatch
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

interface StashClient {
    @POST("api/categories/batch")
    suspend fun updateCategories(@Body categoryList: StashCategoryBatch)

    @POST("api/items/batch")
    suspend fun updateItems(@Body itemList: StashItemBatch)

    @GET("api/categories/batch")
    suspend fun getCategories(): StashCategoryBatch

    @GET("api/items/batch")
    suspend fun getItems(): StashItemBatch
}