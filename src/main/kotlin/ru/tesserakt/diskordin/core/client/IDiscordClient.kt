package ru.tesserakt.diskordin.core.client

import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.rest.RestClient

interface IDiscordClient : IDiscordObject {
    val eventDispatcher: EventDispatcher
    val token: String
    val tokenType: TokenType
    val self: Identified<ISelf>
    val isConnected: Boolean
    val gateway: Gateway
    val rest: RestClient

    /*
    Performs a login to discord servers and enables the Gateway
     */
    suspend fun login()

    /*
    Should be used when need fast connect to Discord. Does not runs the Gateway
     */
    suspend fun use(block: suspend IDiscordClient.() -> Unit)

    fun logout()
    suspend fun findUser(id: Snowflake): IUser?
    suspend fun findGuild(id: Snowflake): IGuild?
    suspend fun findChannel(id: Snowflake): IChannel?
    suspend fun getUser(id: Snowflake): IUser
    suspend fun getGuild(id: Snowflake): IGuild
    suspend fun getChannel(id: Snowflake): IChannel
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
