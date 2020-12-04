package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.entity.IRole
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.PartialGuild
import org.tesserakt.diskordin.impl.core.entity.Role

class RoleUpdater : CacheUpdater<IRole> {
    override fun handle(builder: MemoryCacheSnapshot, data: IRole) = builder.copy(guilds =
    builder.guilds + when (val guild = builder.getGuild(data.guild.id)) {
        is Guild -> arrayOf(guild.id to guild.copy {
            it.copy(roles = it.roles + (data as Role).raw)
        })
        is PartialGuild -> arrayOf(guild.id to guild.copy {
            it.copy(roles = it.roles + (data as Role).raw)
        })
        null -> logger.warn("No guild cached for role update").run { emptyArray() }
        else -> logger.warn("Unknown type of guild found: ${guild::class.simpleName}").run { emptyArray() }
    })
}

internal class RoleDeleter : CacheDeleter<IRole> {
    override fun handle(builder: MemoryCacheSnapshot, data: IRole) = builder.copy(
        guilds = builder.guilds + (when (val guild = builder.getGuild(data.guild.id)) {
            is Guild -> arrayOf(guild.id to guild.copy { it.copy(roles = it.roles - (data as Role).raw) })
            is PartialGuild -> arrayOf(guild.id to guild.copy { it.copy(roles = it.roles - (data as Role).raw) })
            else -> emptyArray()
        })
    )
}