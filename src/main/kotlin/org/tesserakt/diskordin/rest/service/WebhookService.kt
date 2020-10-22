@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest
import org.tesserakt.diskordin.core.data.json.request.WebhookEditRequest
import org.tesserakt.diskordin.core.data.json.response.WebhookResponse
import retrofit2.http.*

interface WebhookService {
    @POST("/api/v6/channels/{id}/webhooks")
    suspend fun createChannelWebhook(
        @Path("id") id: Snowflake,
        @Body request: WebhookCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Id<WebhookResponse>

    @GET("/api/v6/channels/{id}/webhooks")
    suspend fun getChannelWebhooks(@Path("id") id: Snowflake): ListK<WebhookResponse>

    @GET("/api/v6/guilds/{id}/webhooks")
    suspend fun getGuildWebhooks(@Path("id") id: Snowflake): ListK<WebhookResponse>

    @GET("/api/v6/webhooks/{id}")
    suspend fun getWebhook(@Path("id") id: Snowflake): Id<WebhookResponse>

    @PATCH("/api/v6/webhooks/{id}")
    suspend fun editWebhook(
        @Path("id") id: Snowflake,
        @Body request: WebhookEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Id<WebhookResponse>

    @DELETE("/api/v6/webhooks/{id}")
    suspend fun deleteWebhook(@Path("id") id: Snowflake): Unit

    @POST("/api/v6/webhooks/{id}")
    suspend fun executeWebhook(@Path("id") id: Snowflake): Unit
}