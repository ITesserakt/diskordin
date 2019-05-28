package ru.tesserakt.diskordin.impl.core.client

import arrow.core.Option
import arrow.core.handleError
import arrow.core.handleErrorWith
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.request.GuildCreateRequest
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IChannel.Type
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.entity.*
import ru.tesserakt.diskordin.impl.core.rest.service.ChannelService
import ru.tesserakt.diskordin.impl.core.rest.service.GuildService
import ru.tesserakt.diskordin.impl.core.rest.service.UserService
import ru.tesserakt.diskordin.util.Identified
import ru.tesserakt.diskordin.util.ThrowingPolicy

class RestClient(
    private val throwingPolicy: ThrowingPolicy,
    override val token: String,
    override val tokenType: TokenType
) : IDiscordClient {
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
    override lateinit var self: Identified<IUser>
    override var isConnected: Boolean = false
    @FlowPreview
    override val users: Flow<IUser> = emptyArray<IUser>().asFlow() //TODO: stub
    @FlowPreview
    override val guilds: Flow<IGuild> = emptyArray<IGuild>().asFlow() //TODO: same

    override suspend fun login() {
        UserService.General
            .getCurrentUser()
            .map {
                self = Identified(it.id.asSnowflake()) { _ ->
                    coroutineScope.async {
                        User(it, Diskordin.kodein)
                    }
                }
            }.handleError(throwingPolicy::handle)

        isConnected = true
    }

    override fun logout() {}

    override suspend fun findUser(id: Snowflake) =
        UserService.General
            .getUser(id.asLong())
            .map { User(it, Diskordin.kodein) }
            .toOption()

    override suspend fun findGuild(id: Snowflake): Option<IGuild> =
        GuildService.General
            .getGuild(id.asLong())
            .map { Guild(it, Diskordin.kodein) }
            .handleErrorWith(throwingPolicy::handle)
            .toOption()

    override suspend fun findChannel(id: Snowflake): Option<IChannel> =
        ChannelService.General
            .getChannel(id.asLong())
            .map {
                when (Type.of(it.type)) {
                    Type.GuildStore, Type.GuildNews, Type.GuildCategory, Type.PrivateGroup -> TODO()
                    Type.GuildText -> TextChannel(it, Diskordin.kodein)
                    Type.GuildVoice -> VoiceChannel(it, Diskordin.kodein)
                    Type.Private -> PrivateChannel(it, Diskordin.kodein)
                }
            }.toOption()

    override suspend fun createGuild(request: GuildCreateRequest): IGuild =
        GuildService.General
            .createGuild(request)
            .map { Guild(it, Diskordin.kodein) }
            .handleErrorWith(throwingPolicy::handle)
            .fold({ throw it }, { it })
}