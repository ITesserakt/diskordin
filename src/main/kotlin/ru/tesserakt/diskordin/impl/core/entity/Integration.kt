package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.flow.first
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.AccountResponse
import ru.tesserakt.diskordin.core.data.json.response.GuildIntegrationResponse
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IIntegration
import ru.tesserakt.diskordin.core.entity.IRole
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.builder.IntegrationEditBuilder
import ru.tesserakt.diskordin.impl.core.service.GuildService
import ru.tesserakt.diskordin.util.Identified
import java.time.Instant
import java.time.format.DateTimeFormatter

class Integration(
    raw: GuildIntegrationResponse,
    private val guildId: Snowflake
) : IIntegration {
    override val guild: Identified<IGuild> = Identified(guildId) { client.findGuild(it)!! }

    override suspend fun sync() = GuildService.syncIntegration(guildId, id)

    override suspend fun delete(reason: String?) = GuildService.deleteIntegration(guildId, id)

    override suspend fun edit(builder: IntegrationEditBuilder.() -> Unit): IIntegration =
        GuildService.editIntegration(guildId, id, builder).run {
            guild().integrations.first { it.id == id }
        }

    override val type: String = raw.type

    override val enabled: Boolean = raw.enabled

    override val syncing: Boolean = raw.syncing

    override val role: Identified<IRole> = Identified(raw.role_id.asSnowflake()) {
        client.findGuild(guildId)?.findRole(it) ?: throw NoSuchElementException("Guild id isn`t right")
    }

    override val expireBehavior: Int = raw.expire_behavior

    override val expireGracePeriod: Int = raw.expire_grace_period

    override val user: Identified<IUser> = Identified(raw.user.id.asSnowflake()) { User(raw.user) }

    override val account: IIntegration.IAccount = Account(raw.account)

    override val syncedAt: Instant = DateTimeFormatter.ISO_DATE_TIME.parse(raw.synced_at, Instant::from)

    override val id: Snowflake = raw.id.asSnowflake()


    override val name: String = raw.name

    class Account(raw: AccountResponse) : IIntegration.IAccount {
        override val id: Snowflake = raw.id.asSnowflake()


        override val name: String = raw.name
    }
}
