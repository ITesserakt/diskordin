package ru.tesserakt.diskordin.impl.core.entity


import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.WebhookResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.util.Identified

class Webhook(raw: WebhookResponse) : IWebhook {
    override suspend fun delete(reason: String?) =
        webhookService.deleteWebhook(id)

    override val name: String? = raw.name

    override val avatar: String? = raw.avatar

    override val token: String = raw.token

    override val guild: Identified<IGuild>? = raw.guild_id?.let { guildId ->
        Identified(guildId.asSnowflake()) {
            client.findGuild(it) ?: throw NoSuchElementException()
        }
    }

    override val channel: Identified<IChannel> = Identified(raw.channel_id.asSnowflake()) {
        client.findChannel(it) ?: throw NoSuchElementException()
    }

    override val user: Identified<IUser>? = raw.user?.let { user ->
        Identified(user.id.asSnowflake()) {
            User(user)
        }
    }

    override val id: Snowflake = raw.id.asSnowflake()


}