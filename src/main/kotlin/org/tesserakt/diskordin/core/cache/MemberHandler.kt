package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.Member
import org.tesserakt.diskordin.impl.core.entity.PartialGuild

class MemberUpdater : CacheUpdater<IMember> {
    override fun handle(builder: MemoryCacheSnapshot, data: IMember): MemoryCacheSnapshot {
        val guild = builder.getGuild(data.guild.id) ?: return builder
        val newGuild = when {
            guild is Guild && data is Member<*> -> guild.copy {
                it.copy(members = guild.raw.members + data.raw)
            }
            guild is PartialGuild && data is Member<*> -> guild.copy {
                it.copy(members = guild.raw.members + data.raw)
            }
            else -> guild
        }

        return builder.copy(guilds = builder.guilds + (newGuild.id to newGuild))
    }
}

class MemberDeleter : CacheDeleter<IMember> {
    override fun handle(builder: MemoryCacheSnapshot, data: IMember): MemoryCacheSnapshot {
        val guild = builder.getGuild(data.guild.id) ?: return builder
        val newGuild = when {
            guild is Guild && data is Member<*> -> guild.copy {
                it.copy(members = guild.raw.members - data.raw)
            }
            guild is PartialGuild && data is Member<*> -> guild.copy {
                it.copy(members = guild.raw.members - data.raw)
            }
            else -> guild
        }

        return builder.copy(guilds = builder.guilds + (newGuild.id to newGuild))
    }
}