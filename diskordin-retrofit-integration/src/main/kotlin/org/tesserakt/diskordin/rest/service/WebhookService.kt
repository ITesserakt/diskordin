package org.tesserakt.diskordin.rest.service


import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest
import org.tesserakt.diskordin.core.data.json.request.WebhookEditRequest
import org.tesserakt.diskordin.core.data.json.response.WebhookResponse
import retrofit2.http.*

interface WebhookServiceImpl : WebhookService {
    @POST("/api/v6/channels/{id}/webhooks")
    override suspend fun createChannelWebhook(
        @Path("id") id: Snowflake,
        @Body request: WebhookCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): WebhookResponse

    @GET("/api/v6/channels/{id}/webhooks")
    override suspend fun getChannelWebhooks(@Path("id") id: Snowflake): List<WebhookResponse>

    @GET("/api/v6/guilds/{id}/webhooks")
    override suspend fun getGuildWebhooks(@Path("id") id: Snowflake): List<WebhookResponse>

    @GET("/api/v6/webhooks/{id}")
    override suspend fun getWebhook(@Path("id") id: Snowflake): WebhookResponse

    @PATCH("/api/v6/webhooks/{id}")
    override suspend fun editWebhook(
        @Path("id") id: Snowflake,
        @Body request: WebhookEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): WebhookResponse

    @DELETE("/api/v6/webhooks/{id}")
    override suspend fun deleteWebhook(@Path("id") id: Snowflake)

    @POST("/api/v6/webhooks/{id}")
    override suspend fun executeWebhook(@Path("id") id: Snowflake)
}