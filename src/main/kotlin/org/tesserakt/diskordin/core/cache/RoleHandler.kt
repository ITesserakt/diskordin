package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.PartialGuild
import org.tesserakt.diskordin.impl.core.entity.Role

internal class RoleUpdater : CacheUpdater<Role> {
    override fun handle(builder: MemoryCacheSnapshot, data: Role) = builder.copy(guilds =
    builder.guilds + when (val guild = builder.getGuild(data.guild.id)) {
        is Guild -> arrayOf(guild.id to guild.copy {
            it.copy(roles = it.roles + data.raw)
        })
        is PartialGuild -> arrayOf(guild.id to guild.copy {
            it.copy(roles = it.roles + data.raw)
        })
        null -> logger.warn("No guild cached for role update").run { emptyArray() }
        else -> logger.warn("Unknown type of guild found: ${guild::class.simpleName}").run { emptyArray() }
    })
}

internal class RoleDeleter : CacheDeleter<Role> {
    override fun handle(builder: MemoryCacheSnapshot, data: Role) = builder.copy(
        guilds = builder.guilds + (when (val guild = builder.getGuild(data.guild.id)) {
            is Guild -> arrayOf(guild.id to guild.copy { it.copy(roles = it.roles - data.raw) })
            is PartialGuild -> arrayOf(guild.id to guild.copy { it.copy(roles = it.roles - data.raw) })
            else -> emptyArray()
        })
    )
}