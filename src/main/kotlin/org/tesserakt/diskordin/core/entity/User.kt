package org.tesserakt.diskordin.core.entity

import arrow.fx.IO
import kotlinx.coroutines.flow.Flow
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import org.tesserakt.diskordin.util.enums.IValued
import org.tesserakt.diskordin.util.enums.ValuedEnum
import org.tesserakt.diskordin.util.typeclass.Integral
import org.tesserakt.diskordin.util.typeclass.integral

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

    infix fun asMember(guildId: Snowflake): IO<IMember>
}

interface ISelf : IUser, IEditable<ISelf, UserEditBuilder> {
    val guilds: Flow<IGuild>
    val privateChannels: Flow<IPrivateChannel>
    val connections: Flow<IConnection>

    fun leaveGuild(guild: IGuild): IO<Unit>
    fun leaveGuild(guildId: Snowflake): IO<Unit>
    fun joinIntoDM(to: IUser): IO<IPrivateChannel> = joinIntoDM(to.id)
    fun joinIntoDM(to: Snowflake): IO<IPrivateChannel>
    fun edit(username: String, avatar: String) = edit {
        this.username = username
        this.avatar = avatar
    }
}