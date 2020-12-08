package org.tesserakt.diskordin.core.cache

import arrow.core.extensions.map.foldable.firstOption
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.Member
import org.tesserakt.diskordin.impl.core.entity.PartialGuild

internal val MemberUpdater = CacheUpdater<IMember> { builder, data ->
    val guild = builder.getGuild(data.guild.id) ?: return@CacheUpdater
    val newGuild = when {
        guild is Guild && data is Member<*> -> guild.copy {
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