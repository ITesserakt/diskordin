package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.entity.IRole
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.PartialGuild
import org.tesserakt.diskordin.impl.core.entity.Role

internal val RoleUpdater = CacheUpdater<IRole> { builder, data ->
    builder.copy(
        guilds = builder.guilds + when (val guild = builder.getGuild(data.guild.id)) {
            is Guild -> mapOf(guild.id to guild.copy { it.copy(roles = it.roles + (data as Role).raw) })
            is PartialGuild -> mapOf(guild.id to guild.copy { it.copy(roles = it.roles + (data as Role).raw) })
            else -> emptyMap()
        }
    )
}

internal val RoleDeleter = CacheDeleter<IRole> { builder, data ->
    builder.copy(
        guilds = builder.guilds + (when (val guild = builder.getGuild(data.guild.id)) {
            is Guild -> mapOf(guild.id to guild.copy { it.copy(roles = it.roles - (data as Role).raw) })
            is PartialGuild -> mapOf(guild.id to guild.copy { it.copy(roles = it.roles - (data as Role).raw) })
            else -> emptyMap()
        })
    )
}