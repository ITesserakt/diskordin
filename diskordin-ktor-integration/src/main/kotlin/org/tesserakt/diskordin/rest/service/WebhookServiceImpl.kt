package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest
import org.tesserakt.diskordin.core.data.json.request.WebhookEditRequest
import org.tesserakt.diskordin.core.data.json.response.WebhookResponse
import org.tesserakt.diskordin.rest.integration.reasonHeader

class WebhookServiceImpl(private val ktor: HttpClient, private val discordApiUrl: String) : WebhookService {
    override suspend fun createChannelWebhook(
        id: Snowflake,
        request: WebhookCreateRequest,
        reason: String?
    ): Id<WebhookResponse> = ktor.post("$discordApiUrl/api/v6/channels/$id/webhooks") {
        body = request
        reasonHeader(reason)
    }

    override suspend fun getChannelWebhooks(id: Snowflake): ListK<WebhookResponse> =
        ktor.get("$discordApiUrl/api/v6/channels/$id/webhooks")

    override suspend fun getGuildWebhooks(id: Snowflake): ListK<WebhookResponse> =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/webhooks")

    override suspend fun getWebhook(id: Snowflake): Id<WebhookResponse> = ktor.get("$discordApiUrl/api/v6/webhooks/$id")

    override suspend fun editWebhook(id: Snowflake, request: WebhookEditRequest, reason: String?): Id<WebhookResponse> =
        ktor.patch("$discordApiUrl/api/v6/webhooks/$id") {
            body = request
            reasonHeader(reason)
        }

    override suspend fun deleteWebhook(id: Snowflake): Unit = ktor.delete("$discordApiUrl/api/v6/webhooks/$id")

    override suspend fun executeWebhook(id: Snowflake): Unit = ktor.post("$discordApiUrl/api/v6/webhooks/$id")
}