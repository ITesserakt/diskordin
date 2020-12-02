package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.impl.core.entity.Guild
import org.tesserakt.diskordin.impl.core.entity.PartialGuild

class GuildUpdater : CacheUpdater<IGuild> {
    override fun handle(builder: MemoryCacheSnapshot, data: IGuild) = builder.copy(guilds =
    builder.guilds + when (val guild = builder.getGuild(data.id)) {
        is Guild -> when (data) {
            is Guild -> arrayOf(data.id to data)
            is PartialGuild -> arrayOf(data.id to guild.copy {
                it.copy(
                    name = data.raw.name,
                    icon = data.raw.icon,
                    owner = data.raw.owner,
                    permissions = data.raw.permissions,
                    roles = data.raw.roles + it.roles,
                    features = data.raw.features
                )
            })
            else -> emptyArray()
        }
        is PartialGuild -> when (data) {
            is Guild -> arrayOf(data.id to data.copy {
                it.copy(roles = it.roles + guild.raw.roles)
            })
            is PartialGuild -> arrayOf(data.id to guild.copy {
                it.copy(
                    name = data.raw.name,
                    icon = data.raw.icon,
                    owner = data.raw.owner,
                    permissions = data.raw.permissions,
                    features = data.raw.features,
                    roles = data.raw.roles + it.roles
                )
            })
            else -> emptyArray()
        }
        null -> arrayOf(data.id to data)
        else -> logger.warn("Unknown type of guild cached (${guild::class.simpleName}").run { emptyArray() }
    })
}

class GuildDeleter : CacheDeleter<IGuild> {
    override fun handle(builder: MemoryCacheSnapshot, data: IGuild) =
        builder.copy(guilds = builder.guilds - data.id)
}