package ru.tesserakt.diskordin.core.client

import arrow.core.Option
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.GuildCreateRequest
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.util.Identified

interface IDiscordClient {
    val token: String
    val tokenType: TokenType

    val self: Identified<IUser>
    val isConnected: Boolean
    @FlowPreview
    val users: Flow<IUser>
    @FlowPreview
    val guilds: Flow<IGuild>

    val coroutineScope: CoroutineScope

    suspend fun login()

    fun logout()

    suspend fun findUser(id: Snowflake): Option<IUser>

    suspend fun findGuild(id: Snowflake): Option<IGuild>

    suspend fun findChannel(id: Snowflake): Option<IChannel>

    suspend fun createGuild(request: GuildCreateRequest): IGuild
}

enum class TokenType {
    Bot, Bearer, Webhook
}
