package org.tesserakt.diskordin.impl.core.entity


import arrow.fx.fix
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.WebhookResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.IWebhook
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.core.entity.rest

class Webhook(raw: WebhookResponse) : IWebhook {
    override fun delete(reason: String?) = rest.effect {
        webhookService.deleteWebhook(id)
    }.fix()

    override fun toString(): String {
        return StringBuilder("Webhook(")
            .appendln("name=$name, ")
            .appendln("avatar=$avatar, ")
            .appendln("token='$token', ")
            .appendln("guild=$guild, ")
            .appendln("channel=$channel, ")
            .appendln("user=$user, ")
            .appendln("id=$id")
            .appendln(")").toString()
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