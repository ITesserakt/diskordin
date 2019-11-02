package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import ru.tesserakt.diskordin.util.enums.IValued
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import ru.tesserakt.diskordin.util.typeclass.Integral
import ru.tesserakt.diskordin.util.typeclass.integral

interface IUser : IMentioned, INamed {
    val username: String
    override val name: String
        get() = username
    val discriminator: Short
    val isBot: Boolean
    val avatar: String?
    val mfaEnabled: Boolean
    val locale: String?
    val verified: Boolean
    val email: String?
    val flags: ValuedEnum<Flags, Short>
    val premiumType: Type?

    enum class Type {
        NitroClassic, Nitro
    }

    enum class Flags(override val value: Short) : IValued<Flags, Short>, Integral<Short> by Short.integral() {
        None(0),
        DiscordEmployee(1 shl 0),
        DiscordPartner(1 shl 1),
        HypeSquadEvents(1 shl 2),
        BugHunter(1 shl 3),
        HouseBravery(1 shl 6),
        HouseBrilliance(1 shl 7),
        HouseBalance(1 shl 8),
        EarlySupporter(1 shl 9),
        TeamUser(1 shl 10)
    }

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