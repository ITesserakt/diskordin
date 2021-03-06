package org.tesserakt.diskordin.core.cache.handler

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import org.tesserakt.diskordin.core.data.json.response.JoinMemberResponse
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.Member
import org.tesserakt.diskordin.impl.core.entity.PartialGuild

private val Snowflake.memberResponseIso
    get() = { it: JoinMemberResponse ->
        GuildMemberResponse(it.user, it.nick, it.roles, it.joinedAt, it.deaf, it.mute)
    } to { it: GuildMemberResponse ->
        JoinMemberResponse(it.user, it.nick, it.roles, it.joinedAt, it.deaf, it.mute, this)
    }

internal val MemberUpdater = CacheUpdater<IMember> { builder, data ->
    val guild = builder.getGuild(data.guild.id) ?: return@CacheUpdater
    val newGuild = when {
        guild is Guild && data is Member<*> && data.raw is JoinMemberResponse -> guild.copy {
            it.copy(members = guild.raw.members + guild.id.memberResponseIso.first(data.raw))
        }
        guild is Guild && data is Member<*> && data.raw is GuildMemberResponse -> guild.copy {
            it.copy(members = guild.raw.members + data.raw)
        }
        guild is PartialGuild && data is Member<*> && data.raw is JoinMemberResponse -> guild.copy {
            it.copy(members = guild.raw.members + guild.id.memberResponseIso.first(data.raw))
        }
        guild is PartialGuild && data is Member<*> && data.raw is GuildMemberResponse -> guild.copy {
            it.copy(members = guild.raw.members + data.raw)
        }
        else -> guild
    }

    builder.guilds += (newGuild.id to newGuild)
}

internal val MemberDeleter = CacheDeleter<IMember> { builder, data ->
    val guild = builder.guilds.values.firstOrNull { data in it.members.map(IEntity::id) } ?: return@CacheDeleter
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