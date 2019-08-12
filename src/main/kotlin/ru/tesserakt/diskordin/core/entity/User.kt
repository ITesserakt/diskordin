package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.UserEditBuilder

interface IUser : IMentioned, INamed {
    val username: String
    override val name: String
        get() = username
    val discriminator: Short
    val isBot: Boolean

    suspend infix fun asMember(guildId: Snowflake): IMember
}

interface ISelf : IUser, IEditable<ISelf, UserEditBuilder> {
    val guilds: Flow<IGuild>
    val privateChannels: Flow<IPrivateChannel>
    val connections: Flow<IConnection>

    suspend fun leaveGuild(guild: IGuild)
    suspend fun leaveGuild(guildId: Snowflake)
    suspend fun joinIntoDM(builder: DMCreateBuilder.() -> Unit): IChannel
}