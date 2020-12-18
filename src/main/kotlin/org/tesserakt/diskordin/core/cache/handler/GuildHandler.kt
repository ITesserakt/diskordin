package org.tesserakt.diskordin.core.cache.handler

import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.PartialGuild

internal val GuildUpdater = CacheUpdater<IGuild> { builder, data ->
    val guild = builder.getGuild(data.id)
    builder.guilds += when {
        guild is Guild && data is Guild -> arrayOf(data.id to data)
        guild is Guild && data is PartialGuild -> arrayOf(data.id to guild.copy {
            it.copy(
                name = data.raw.name,
                icon = data.raw.icon,
                owner = data.raw.owner,
                permissions = data.raw.permissions,
                roles = data.raw.roles + it.roles,
                features = data.raw.features
            )
        })
        guild is PartialGuild && data is Guild -> arrayOf(data.id to data.copy {
            it.copy(roles = it.roles + guild.raw.roles)
        })
        guild is PartialGuild && data is PartialGuild -> arrayOf(data.id to guild.copy {
            it.copy(
                name = data.raw.name,
                icon = data.raw.icon,
                owner = data.raw.owner,
                permissions = data.raw.permissions,
                features = data.raw.features,
                roles = data.raw.roles + it.roles
            )
        })
        guild == null -> arrayOf(data.id to data)
        else -> emptyArray()
    }
}

internal val GuildDeleter = CacheDeleter<IGuild> { builder, data ->
    builder.guilds -= data
}