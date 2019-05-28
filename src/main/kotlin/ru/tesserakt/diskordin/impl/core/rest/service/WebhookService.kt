@file:Suppress("unused")

package ru.tesserakt.diskordin.impl.core.rest.service

import ru.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.WebhookEditRequest
import ru.tesserakt.diskordin.core.data.json.response.WebhookResponse
import ru.tesserakt.diskordin.impl.core.rest.Routes
import ru.tesserakt.diskordin.util.append

internal object WebhookService {
    object General {

        suspend fun createChannelWebhook(channelId: Long, request: WebhookCreateRequest, reason: String?) =
            Routes.createChannelWebhook(channelId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<WebhookResponse>(request)


        suspend fun getChannelWebhooks(channelId: Long) =
            Routes.getChannelWebhooks(channelId)
                .newRequest()
                .resolve<Array<WebhookResponse>>()


        suspend fun getGuildWebhooks(guildId: Long) =
            Routes.getGuildWebhooks(guildId)
                .newRequest()
                .resolve<WebhookResponse>()


        suspend fun getWebhook(webhookId: Long) =
            Routes.getWebhook(webhookId)
                .newRequest()
                .resolve<WebhookResponse>()

        suspend fun editWebhook(webhookId: Long, request: WebhookEditRequest, reason: String?) =
            Routes.modifyWebhook(webhookId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<WebhookResponse>(request)

        suspend fun removeWebhook(webhookId: Long, reason: String?) =
            Routes.deleteWebhook(webhookId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>()
    }
}