package ru.tesserakt.diskordin.impl.core.entity


import arrow.fx.fix
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.WebhookResponse
import ru.tesserakt.diskordin.core.entity.*

class Webhook(raw: WebhookResponse) : IWebhook {
    override suspend fun delete(reason: String?) = rest.effect {
        webhookService.deleteWebhook(id)
    }.fix().suspended()

    override fun toString(): String {
        return "Webhook(name=$name, avatar=$avatar, token='$token', guild=$guild, channel=$channel, user=$user, id=$id)"
    }

    override val name: String? = raw.name

    override val avatar: String? = raw.avatar

    override val token: String = raw.token

    override val guild: Identified<IGuild>? = raw.guild_id?.let { guildId ->
        Identified(guildId) {
            client.findGuild(it) ?: throw NoSuchElementException()
        }
    }

    override val channel: Identified<IChannel> =
        Identified(raw.channel_id) {
            client.findChannel(it) ?: throw NoSuchElementException()
        }

    override val user: Identified<IUser>? = raw.user?.let { user ->
        Identified(user.id) {
            User(user)
        }
    }

    override val id: Snowflake = raw.id
}