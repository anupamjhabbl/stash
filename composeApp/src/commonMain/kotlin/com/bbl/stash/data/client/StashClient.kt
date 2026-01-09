package com.bbl.stash.data.client

import com.bbl.stash.auth.entity.BaseResponse
import com.bbl.stash.data.client.model.StashCategoryBatch
import com.bbl.stash.data.client.model.StashDeleteCategories
import com.bbl.stash.data.client.model.StashDeleteItems
import com.bbl.stash.data.client.model.StashItemBatch
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

interface StashClient {
    @POST("api/categories/batch")
    suspend fun updateCategories(@Body categoryList: StashCategoryBatch): BaseResponse<Unit>

    @POST("api/items/batch")
    suspend fun updateItems(@Body itemList: StashItemBatch): BaseResponse<Unit>

    @GET("api/categories/batch")
    suspend fun getCategories(): BaseResponse<StashCategoryBatch>

    @GET("api/items/batch")
    suspend fun getItems(): BaseResponse<StashItemBatch>

    @DELETE("api/categories/batch")
    suspend fun deleteCategories(@Body stashDeleteCategories: StashDeleteCategories): BaseResponse<Unit>

    @DELETE("api/items/batch")
    suspend fun deleteItems(@Body stashDeleteItems: StashDeleteItems): BaseResponse<Unit>
}