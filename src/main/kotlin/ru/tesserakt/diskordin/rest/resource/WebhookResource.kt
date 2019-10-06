@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.resource

import ru.tesserakt.diskordin.core.data.json.request.WebhookCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.WebhookEditRequest
import ru.tesserakt.diskordin.rest.Routes

internal object WebhookResource {
    object General {
        suspend fun createChannelWebhook(channelId: Long, request: WebhookCreateRequest, reason: String?) =
            Routes.createChannelWebhook(channelId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve(request)


        suspend fun getChannelWebhooks(channelId: Long) =
            Routes.getChannelWebhooks(channelId)
                .newRequest()
                .resolve()


        suspend fun getGuildWebhooks(guildId: Long) =
            Routes.getGuildWebhooks(guildId)
                .newRequest()
                .resolve()


        suspend fun getWebhook(webhookId: Long) =
            Routes.getWebhook(webhookId)
                .newRequest()
                .resolve()

        suspend fun editWebhook(webhookId: Long, request: WebhookEditRequest, reason: String?) =
            Routes.modifyWebhook(webhookId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve(request)

        suspend fun removeWebhook(webhookId: Long, reason: String?) =
            Routes.deleteWebhook(webhookId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()
    }
}