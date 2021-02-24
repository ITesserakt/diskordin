package org.tesserakt.diskordin.impl.core.entity


import org.tesserakt.diskordin.core.data.*
import org.tesserakt.diskordin.core.data.json.response.WebhookResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*

internal class Webhook(raw: WebhookResponse) : IWebhook {
    override suspend fun delete(reason: String?) = rest.effect {
        webhookService.deleteWebhook(id)
    }

    override fun toString(): String {
        return "Webhook(name=$name, avatar=$avatar, token='$token', guild=$guild, channel=$channel, user=$user, id=$id)"
    }

    override val name: String? = raw.name

    override val avatar: String? = raw.avatar

    override val token: String = raw.token

    override val guild: DeferredIdentified<IGuild>? = raw.guild_id?.deferred {
        client.getGuild(it)
    }

    override val channel: DeferredIdentified<IChannel> = raw.channel_id deferred {
        client.getChannel(it)
    }

    override val user: EagerIdentified<IUser>? = raw.user?.id?.eager { raw.user.unwrap() }

    override val id: Snowflake = raw.id
}