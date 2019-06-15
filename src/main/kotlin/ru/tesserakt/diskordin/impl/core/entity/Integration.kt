package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
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
    private val guildId: Snowflake,
    override val kodein: Kodein = Diskordin.kodein
) : IIntegration {
    override val guild: Identified<IGuild> = Identified(guildId) {
        client.coroutineScope.async { client.findGuild(it)!! }
    }

    override suspend fun sync() = GuildService.syncIntegration(guildId, id)

    override suspend fun delete(reason: String?) = GuildService.deleteIntegration(guildId, id)

    @ExperimentalCoroutinesApi
    override suspend fun edit(builder: IntegrationEditBuilder.() -> Unit): IIntegration =
        GuildService.editIntegration(guildId, id, builder).run {
            guild.extract().await().integrations.first { it.id == id }
        }

    override val type: String = raw.type

    override val enabled: Boolean = raw.enabled

    override val syncing: Boolean = raw.syncing

    override val role: Identified<IRole> = Identified(raw.role_id.asSnowflake()) {
        client.coroutineScope.async {
            client.findGuild(guildId)?.findRole(it) ?: throw NoSuchElementException("Guild id isn`t right")
        }
    }

    override val expireBehavior: Int = raw.expire_behavior

    override val expireGracePeriod: Int = raw.expire_grace_period

    override val user: Identified<IUser> = Identified(raw.user.id.asSnowflake()) {
        client.coroutineScope.async {
            User(raw.user)
        }
    }

    override val account: IIntegration.IAccount = Account(raw.account)

    override val syncedAt: Instant = DateTimeFormatter.ISO_DATE_TIME.parse(raw.synced_at, Instant::from)

    override val id: Snowflake = raw.id.asSnowflake()

    override val client: IDiscordClient by instance()

    override val name: String = raw.name

    class Account(raw: AccountResponse, override val kodein: Kodein = Diskordin.kodein) : IIntegration.IAccount {
        override val id: Snowflake = raw.id.asSnowflake()

        override val client: IDiscordClient by instance()

        override val name: String = raw.name
    }
}
