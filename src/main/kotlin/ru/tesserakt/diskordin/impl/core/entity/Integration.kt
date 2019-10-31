package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.flow.first
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.json.response.AccountResponse
import ru.tesserakt.diskordin.core.data.json.response.GuildIntegrationResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.IntegrationEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import java.time.Instant
import java.time.format.DateTimeFormatter

class Integration(
    raw: GuildIntegrationResponse,
    guildId: Snowflake
) : IIntegration {
    override suspend fun sync() = guildService.syncIntegration(guild.id, id)

    override val guild: Identified<IGuild> = guildId combine { client.getGuild(it) }

    override suspend fun delete(reason: String?) = guildService.deleteIntegration(guild.id, id)

    override suspend fun edit(builder: IntegrationEditBuilder.() -> Unit): IIntegration =
        guildService.editIntegration(guild.id, id, builder.build()).run {
            guild().integrations.first { it.id == id }
        }

    override fun toString(): String {
        return "Integration(guild=$guild, type='$type', enabled=$enabled, syncing=$syncing, role=$role, expireBehavior=$expireBehavior, expireGracePeriod=$expireGracePeriod, user=$user, account=$account, syncedAt=$syncedAt, id=$id, name='$name')"
    }

    override val type: String = raw.type

    override val enabled: Boolean = raw.enabled

    override val syncing: Boolean = raw.syncing

    override val role = raw.role_id combine { guild().roles.first { role -> role.id == it } }

    override val expireBehavior: Int = raw.expire_behavior

    override val expireGracePeriod: Int = raw.expire_grace_period

    override val user: Identified<IUser> =
        Identified(raw.user.id) { User(raw.user) }

    override val account: IIntegration.IAccount = Account(raw.account)

    override val syncedAt: Instant = DateTimeFormatter.ISO_DATE_TIME.parse(raw.synced_at, Instant::from)

    override val id: Snowflake = raw.id


    override val name: String = raw.name

    class Account(raw: AccountResponse) : IIntegration.IAccount {
        override val id: Snowflake = raw.id

        override val name: String = raw.name
    }
}
