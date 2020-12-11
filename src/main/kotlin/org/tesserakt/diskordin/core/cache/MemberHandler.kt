package org.tesserakt.diskordin.core.cache

import arrow.core.extensions.map.foldable.firstOption
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import org.tesserakt.diskordin.core.data.json.response.JoinMemberResponse
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.Member
import org.tesserakt.diskordin.impl.core.entity.PartialGuild

internal val MemberUpdater = CacheUpdater<IMember> { builder, data ->
    val guild = builder.getGuild(data.guild.id) ?: return@CacheUpdater
    val newGuild = when {
        guild is Guild && data is Member<*> && data.raw is JoinMemberResponse -> guild.copy {
            it.copy(
                members = guild.raw.members + GuildMemberResponse(
                    data.raw.user,
                    data.raw.nick,
                    data.raw.roles,
                    data.raw.joinedAt,
                    data.raw.deaf,
                    data.raw.mute
                )
            )
        }
        guild is Guild && data is Member<*> && data.raw is GuildMemberResponse -> guild.copy {
            it.copy(members = guild.raw.members + data.raw)
        }
        guild is PartialGuild && data is Member<*> -> guild.copy {
            it.copy(members = guild.raw.members + data.raw)
        }
        else -> guild
    }

    builder.guilds += (newGuild.id to newGuild)
}

internal val MemberDeleter = CacheDeleter<IMember> { builder, data ->
    val guild = builder.guilds.firstOption { data in it.members.map(IEntity::id) }.orNull() ?: return@CacheDeleter
    val newGuild = when (guild) {
        is Guild -> guild.copy {
            it.copy(members = guild.raw.members.filter { member -> member.user.id == data }.toSet())
        }
        is PartialGuild -> guild.copy {
            it.copy(members = guild.raw.members.filter { member -> member.user.id == data }.toSet())
        }
        else -> guild
    }

    builder.guilds += (newGuild.id to newGuild)
}