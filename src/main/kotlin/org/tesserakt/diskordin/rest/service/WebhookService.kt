@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest
import org.tesserakt.diskordin.core.data.json.request.WebhookEditRequest
import org.tesserakt.diskordin.core.data.json.response.WebhookResponse

interface WebhookService {
    suspend fun createChannelWebhook(
        id: Snowflake,
        request: WebhookCreateRequest,
        reason: String?
    ): WebhookResponse

    suspend fun getChannelWebhooks(id: Snowflake): ListK<WebhookResponse>

    suspend fun getGuildWebhooks(id: Snowflake): ListK<WebhookResponse>

    suspend fun getWebhook(id: Snowflake): WebhookResponse

    suspend fun editWebhook(
        id: Snowflake,
        request: WebhookEditRequest,
        reason: String?
    ): WebhookResponse

    suspend fun deleteWebhook(id: Snowflake)

    suspend fun executeWebhook(id: Snowflake)
}