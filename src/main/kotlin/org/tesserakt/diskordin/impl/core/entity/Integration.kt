package org.tesserakt.diskordin.impl.core.entity


import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.fix
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.AccountResponse
import org.tesserakt.diskordin.core.data.json.response.GuildIntegrationResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IIntegration
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.builder.IntegrationEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.core.entity.rest
import java.time.Instant
import java.time.format.DateTimeFormatter

class Integration(
    raw: GuildIntegrationResponse,
    guildId: Snowflake
) : IIntegration {
    override fun sync() = rest.effect {
        guildService.syncIntegration(guild.id, id)
    }.fix()

    override val guild = guildId identify { client.getGuild(it).bind() }

    override fun delete(reason: String?) = rest.effect {
        guildService.deleteIntegration(guild.id, id)
    }.fix()

    override fun edit(builder: IntegrationEditBuilder.() -> Unit): IO<IIntegration> = rest.effect {
        guildService.editIntegration(guild.id, id, builder.build())
    }.flatMap { IO.fx { guild().bind().integrations.bind().first { it.id == id } } }

    override fun toString(): String {
        return StringBuilder("Integration(")
            .appendln("guild=$guild, ")
            .appendln("type='$type', ")
            .appendln("enabled=$enabled, ")
            .appendln("syncing=$syncing, ")
            .appendln("role=$role, ")
            .appendln("expireBehavior=$expireBehavior, ")
            .appendln("expireGracePeriod=$expireGracePeriod, ")
            .appendln("user=$user, ")
            .appendln("account=$account, ")
            .appendln("syncedAt=$syncedAt, ")
            .appendln("id=$id, ")
            .appendln("name='$name'")
            .appendln(")")
            .toString()
    }

    override val type: String = raw.type

    override val enabled: Boolean = raw.enabled

    override val syncing: Boolean = raw.syncing

    override val role = raw.role_id identify { guild().bind().roles.bind().first { role -> role.id == it } }

    override val expireBehavior: Int = raw.expire_behavior

    override val expireGracePeriod: Int = raw.expire_grace_period

    override val user: Identified<IUser> = raw.user.id identify { raw.user.unwrap() }

    override val account: IIntegration.IAccount = Account(raw.account)

    override val syncedAt: Instant = DateTimeFormatter.ISO_DATE_TIME.parse(raw.synced_at, Instant::from)

    override val id: Snowflake = raw.id


    override val name: String = raw.name

    class Account(raw: AccountResponse) : IIntegration.IAccount {
        override val id: Snowflake = raw.id

        override val name: String = raw.name
    }
}
