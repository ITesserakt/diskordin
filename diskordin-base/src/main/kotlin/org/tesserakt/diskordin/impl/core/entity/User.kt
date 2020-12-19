@file:Suppress("DEPRECATION")

package org.tesserakt.diskordin.impl.core.entity

import arrow.core.Id
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import org.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.core.entity.query.UserGuildsQuery
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.rest.call
import org.tesserakt.diskordin.rest.flow
import org.tesserakt.diskordin.util.enums.ValuedEnum

abstract class User(raw: UserResponse<IUser>) : IUser {
    final override val avatar: String? = raw.avatar

    final override val mfaEnabled: Boolean = raw.mfa_enabled ?: false

    final override val locale: String? = raw.locale

    final override val flags: ValuedEnum<IUser.Flags, Int> = ValuedEnum(raw.publicFlags, Int.integral())

    final override val premiumType: IUser.Type = when (raw.premium_type) {
        1 -> IUser.Type.NitroClassic
        2 -> IUser.Type.Nitro
        else -> IUser.Type.None
    }

    final override val isFullyLoaded: Boolean = true

    final override val username: String = raw.username

    final override val discriminator: Short = raw.discriminator

    final override val isBot: Boolean = raw.bot

    final override val isSystem = raw.system

    final override val id: Snowflake = raw.id

    @Suppress("CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION")
    override suspend fun asMember(guildId: Snowflake): IMember = rest.call(guildId, Id.functor()) {
        guildService.getMember(guildId, id).just()
    }.extract()

    override val mention: String = "<@${id}>"
}

class Self(override val raw: UserResponse<ISelf>) : User(raw), ISelf,
    ICacheable<IUser, UnwrapContext.EmptyContext, UserResponse<IUser>> {
    override suspend fun joinIntoDM(to: Snowflake): IPrivateChannel = rest.call {
        userService.joinToDM(DMCreateBuilder(to).create())
    }

    override val guilds = rest.flow {
        userService.getCurrentUserGuilds(UserGuildsQuery().create())
    }

    override val privateChannels = rest.flow {
        userService.getUserDMs()
    }

    override val connections = rest.flow {
        userService.getCurrentUserConnections()
    }

    override suspend fun leaveGuild(guild: IGuild) = leaveGuild(guild.id)

    override suspend fun leaveGuild(guildId: Snowflake) = rest.effect {
        userService.leaveGuild(guildId)
    }

    override suspend fun edit(builder: UserEditBuilder.() -> Unit): ISelf = rest.call {
        userService.editCurrentUser(builder.build(::UserEditBuilder))
    }

    override fun toString(): String {
        return "Self(guilds=$guilds, privateChannels=$privateChannels, connections=$connections) " +
                "\n   ${super.toString()}"
    }

    override fun copy(changes: (UserResponse<IUser>) -> UserResponse<IUser>): IUser = raw.run(changes).unwrap()
}