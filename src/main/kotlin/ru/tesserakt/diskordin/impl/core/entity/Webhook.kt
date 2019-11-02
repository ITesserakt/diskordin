package ru.tesserakt.diskordin.impl.core.entity


import arrow.fx.fix
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.WebhookResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.IWebhook
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.core.entity.rest

class Webhook(raw: WebhookResponse) : IWebhook {
    override fun delete(reason: String?) = rest.effect {
        webhookService.deleteWebhook(id)
    }.fix()

    override fun toString(): String {
        return "Webhook(name=$name, avatar=$avatar, token='$token', guild=$guild, channel=$channel, user=$user, id=$id)"
    }

    override val name: String? = raw.name

    override val avatar: String? = raw.avatar

    override val token: String = raw.token

    override val guild = raw.guild_id?.identify {
        client.getGuild(it).bind()
    }

    override val channel = raw.channel_id identify {
        client.getChannel(it).bind()
    }

    override val user: Identified<IUser>? = raw.user?.id?.identify { raw.user.unwrap() }

    override val id: Snowflake = raw.id
}