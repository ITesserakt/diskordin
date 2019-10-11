@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.service

import retrofit2.http.*
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.WebhookEditRequest
import ru.tesserakt.diskordin.core.data.json.response.WebhookResponse

interface WebhookService {
    @POST("/api/v6/channels/{id}/webhooks")
    suspend fun createChannelWebhook(
        @Path("id") id: Snowflake,
        @Body request: WebhookCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): WebhookResponse

    @GET("/api/v6/channels/{id}/webhooks")
    suspend fun getChannelWebhooks(@Path("id") id: Snowflake): Array<WebhookResponse>

    @GET("/api/v6/guilds/{id}/webhooks")
    suspend fun getGuildWebhooks(@Path("id") id: Snowflake): Array<WebhookResponse>

    @GET("/api/v6/webhooks/{id}")
    suspend fun getWebhook(@Path("id") id: Snowflake): WebhookResponse

    @PATCH("/api/v6/webhooks/{id}")
    suspend fun editWebhook(
        @Path("id") id: Snowflake,
        @Body request: WebhookEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): WebhookResponse

    @DELETE("/api/v6/webhooks/{id}")
    suspend fun deleteWebhook(@Path("id") id: Snowflake)

    @POST("/api/v6/webhooks/{id}")
    suspend fun executeWebhook(@Path("id") id: Snowflake)
}