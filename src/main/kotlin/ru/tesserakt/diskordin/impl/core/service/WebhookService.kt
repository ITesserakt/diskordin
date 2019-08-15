@file:Suppress("unused")

package ru.tesserakt.diskordin.impl.core.service

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IWebhook
import ru.tesserakt.diskordin.core.entity.builder.WebhookCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.WebhookEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.core.entity.builder.extractReason
import ru.tesserakt.diskordin.impl.core.entity.Webhook
import ru.tesserakt.diskordin.rest.resource.WebhookResource

internal object WebhookService {
    suspend fun createChannelWebhook(channelId: Snowflake, builder: WebhookCreateBuilder.() -> Unit): IWebhook =
        Webhook(
            WebhookResource.General.createChannelWebhook(
                channelId.asLong(),
                builder.build(),
                builder.extractReason()
            )
        )

    suspend fun getChannelWebhooks(channelId: Snowflake): List<IWebhook> =
        WebhookResource.General.getChannelWebhooks(channelId.asLong())
            .map { Webhook(it) }

    suspend fun getGuildWebhooks(guildId: Snowflake): List<IWebhook> =
        WebhookResource.General.getGuildWebhooks(guildId.asLong())
            .map { Webhook(it) }

    suspend fun getWebhook(id: Snowflake): IWebhook =
        Webhook(WebhookResource.General.getWebhook(id.asLong()))

    suspend fun editWebhook(id: Snowflake, builder: WebhookEditBuilder.() -> Unit): IWebhook =
        Webhook(WebhookResource.General.editWebhook(id.asLong(), builder.build(), builder.extractReason()))

    suspend fun removeWebhook(id: Snowflake, reason: String?) =
        WebhookResource.General.removeWebhook(id.asLong(), reason)
}