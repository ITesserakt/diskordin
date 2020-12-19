package org.tesserakt.diskordin.impl.core.entity

import arrow.core.ForId
import kotlinx.coroutines.flow.first
import org.tesserakt.diskordin.core.data.*
import org.tesserakt.diskordin.core.data.json.response.AccountResponse
import org.tesserakt.diskordin.core.data.json.response.GuildIntegrationResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.IntegrationEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build

internal class Integration(
    raw: GuildIntegrationResponse,
    guildId: Snowflake
) : IIntegration {
    override suspend fun sync() = rest.effect {
        guildService.syncIntegration(guild.id, id)
    }

    override val guild = guildId.identify<IGuild> { client.getGuild(it) }

    override suspend fun delete(reason: String?) = rest.effect {
        guildService.deleteIntegration(guild.id, id)
    }

    override suspend fun edit(builder: IntegrationEditBuilder.() -> Unit) = rest.effect {
        guildService.editIntegration(guild.id, id, builder.build(::IntegrationEditBuilder))
    }.let { guild().integrations.first { it.id == id } }

    override fun toString(): String {
        return "Integration(guild=$guild, type='$type', enabled=$enabled, syncing=$syncing, role=$role, expireBehavior=$expireBehavior, expireGracePeriod=$expireGracePeriod, user=$user, account=$account, syncedAt=$syncedAt, id=$id, name='$name')"
    }

    override val type: String = raw.type

    override val enabled: Boolean = raw.enabled

    override val syncing: Boolean = raw.syncing

    override val role = raw.role_id.identify<IRole> { id ->
        guild().getRole(id)!!
    }

    override val expireBehavior: Int = raw.expire_behavior

    override val expireGracePeriod: Int = raw.expire_grace_period

    override val user: IdentifiedF<ForId, IUser> = raw.user.id identifyId { raw.user.unwrap() }

    override val account: IIntegration.IAccount = Account(raw.account)

    override val syncedAt = raw.synced_at

    override val id: Snowflake = raw.id


    override val name: String = raw.name

    class Account(raw: AccountResponse) : IIntegration.IAccount {
        override val id: Snowflake = raw.id

        override val name: String = raw.name
    }
}
