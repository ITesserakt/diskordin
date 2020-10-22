package org.tesserakt.diskordin.core.entity

import kotlinx.coroutines.flow.Flow
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.IValued
import org.tesserakt.diskordin.util.enums.ValuedEnum
import org.tesserakt.diskordin.util.typeclass.Integral

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
    val flags: ValuedEnum<Flags, Int>
    val premiumType: Type?

    companion object : StaticMention<IUser, Companion> {
        override val mention: Regex = Regex(""""<@(\d{18,})>"""")
    }

    enum class Type {
        NitroClassic, Nitro
    }

    enum class Flags(override val code: Int) : IValued<Flags, Int>, Integral<Int> by Int.integral() {
        DiscordEmployee(1 shl 0),
        DiscordPartner(1 shl 1),
        HypeSquadEvents(1 shl 2),
        BugHunterLVL1(1 shl 3),
        HouseBravery(1 shl 6),
        HouseBrilliance(1 shl 7),
        HouseBalance(1 shl 8),
        EarlySupporter(1 shl 9),
        TeamUser(1 shl 10),
        System(1 shl 12),
        BugHunterLVL2(1 shl 14),
        VerifiedBot(1 shl 16),
        VerifiedBotDeveloper(1 shl 17)
    }

    suspend infix fun asMember(guildId: Snowflake): IMember
}

interface ISelf : IUser, IEditable<ISelf, UserEditBuilder> {
    val guilds: Flow<IGuild>
    val privateChannels: Flow<IPrivateChannel>
    val connections: Flow<IConnection>

    suspend fun leaveGuild(guild: IGuild)
    suspend fun leaveGuild(guildId: Snowflake)
    suspend fun joinIntoDM(to: IUser): IPrivateChannel = joinIntoDM(to.id)
    suspend fun joinIntoDM(to: Snowflake): IPrivateChannel
    suspend fun edit(username: String, avatar: String) = edit {
        this.username = username
        this.avatar = avatar
    }
}