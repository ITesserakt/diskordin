package org.tesserakt.diskordin.impl.core.entity


import arrow.core.ForId
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
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

    override val guild = raw.guild_id?.identify<IGuild> {
        client.getGuild(it)
    }

    override val channel = raw.channel_id.identify<IChannel> {
        client.getChannel(it)
    }

    override val user: IdentifiedF<ForId, IUser>? = raw.user?.id?.identifyId { raw.user.unwrap() }

    override val id: Snowflake = raw.id
}