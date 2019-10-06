package ru.tesserakt.diskordin.core.client

import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.ISelf
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.util.Identified

interface IDiscordClient : KoinComponent {
    val token: String
    val tokenType: TokenType
    val self: Identified<ISelf>
    val isConnected: Boolean
    val gateway: Gateway

    suspend fun login()
    suspend fun use(block: suspend IDiscordClient.() -> Unit)
    fun logout()
    suspend fun findUser(id: Snowflake): IUser?
    suspend fun findGuild(id: Snowflake): IGuild?
    suspend fun findChannel(id: Snowflake): IChannel?
    suspend fun createGuild(request: GuildCreateBuilder.() -> Unit): IGuild
    suspend fun getInvite(code: String): IInvite?
    suspend fun deleteInvite(code: String, reason: String?)
    suspend fun getRegions(): List<IRegion>

    val users: Flow<IUser>
    val guilds: Flow<IGuild>
}

enum class TokenType {
    Bot, Bearer, Webhook
}