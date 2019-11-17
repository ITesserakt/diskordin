@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import arrow.integrations.retrofit.adapter.CallK
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest
import org.tesserakt.diskordin.core.data.json.request.WebhookEditRequest
import org.tesserakt.diskordin.core.data.json.response.WebhookResponse
import retrofit2.http.*

interface WebhookService {
    @POST("/api/v6/channels/{id}/webhooks")
    fun createChannelWebhook(
        @Path("id") id: Snowflake,
        @Body request: WebhookCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Id<WebhookResponse>>

    @GET("/api/v6/channels/{id}/webhooks")
    fun getChannelWebhooks(@Path("id") id: Snowflake): CallK<ListK<WebhookResponse>>

    @GET("/api/v6/guilds/{id}/webhooks")
    fun getGuildWebhooks(@Path("id") id: Snowflake): CallK<ListK<WebhookResponse>>

    @GET("/api/v6/webhooks/{id}")
    fun getWebhook(@Path("id") id: Snowflake): CallK<Id<WebhookResponse>>

    @PATCH("/api/v6/webhooks/{id}")
    fun editWebhook(
        @Path("id") id: Snowflake,
        @Body request: WebhookEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Id<WebhookResponse>>

    @DELETE("/api/v6/webhooks/{id}")
    fun deleteWebhook(@Path("id") id: Snowflake): CallK<Unit>

    @POST("/api/v6/webhooks/{id}")
    fun executeWebhook(@Path("id") id: Snowflake): CallK<Unit>
}