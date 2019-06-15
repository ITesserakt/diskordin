package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.async
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.WebhookResponse
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.IWebhook
import ru.tesserakt.diskordin.impl.core.rest.resource.WebhookResource
import ru.tesserakt.diskordin.util.Identified

class Webhook(raw: WebhookResponse, override val kodein: Kodein = Diskordin.kodein) : IWebhook {
    override suspend fun delete(reason: String?) =
        WebhookResource.General.removeWebhook(id.asLong(), reason)

    override val name: String? = raw.name

    override val avatar: String? = raw.avatar

    override val token: String = raw.token

    override val guild: Identified<IGuild>? = raw.guild_id?.let { guildId ->
        Identified(guildId.asSnowflake()) {
            client.coroutineScope.async {
                client.findGuild(it) ?: throw NoSuchElementException()
            }
        }
    }

    override val channel: Identified<IChannel> = Identified(raw.channel_id.asSnowflake()) {
        client.coroutineScope.async {
            client.findChannel(it) ?: throw NoSuchElementException()
        }
    }

    override val user: Identified<IUser>? = raw.user?.let { user ->
        Identified(user.id.asSnowflake()) {
            client.coroutineScope.async {
                User(user)
            }
        }
    }

    override val id: Snowflake = raw.id.asSnowflake()

    override val client: IDiscordClient by instance()
}