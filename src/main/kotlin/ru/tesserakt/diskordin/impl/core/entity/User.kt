package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import ru.tesserakt.diskordin.impl.core.service.UserService

open class User(raw: UserResponse) : IUser {
    final override val username: String = raw.username

    final override val discriminator: Short = raw.discriminator.toShort()

    final override val isBot: Boolean = raw.bot ?: false

    final override val id: Snowflake = raw.id.asSnowflake()

    final override val mention: String = "<@$id>"
}

class Self(raw: UserResponse) : User(raw), ISelf {
    @ExperimentalCoroutinesApi
    override val guilds: Flow<IGuild>
        get() = flow {
            UserService.getCurrentUserGuilds { this.limit = 100 }.forEach { emit(it) }
        }
    @ExperimentalCoroutinesApi
    override val privateChannels: Flow<IPrivateChannel>
        get() = flow {
            UserService.getCurrentUserPrivateChannels().forEach { emit(it) }
        }
    @ExperimentalCoroutinesApi
    override val connections: Flow<IConnection>
        get() = flow {
            UserService.getCurrentUserConnections().forEach { emit(it) }
        }

    override suspend fun leaveGuild(guild: IGuild) = leaveGuild(guild.id)

    override suspend fun leaveGuild(guildId: Snowflake) = UserService.leaveGuild(guildId)

    override suspend fun joinIntoDM(builder: DMCreateBuilder.() -> Unit): IChannel = UserService.joinIntoDM(builder)

    override suspend fun edit(builder: UserEditBuilder.() -> Unit): ISelf = UserService.editCurrentUser(builder)
}