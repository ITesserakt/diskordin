package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild
import java.time.Instant

data class MemoryCacheSnapshot(
    override val privateChannels: SnowflakeMap<IPrivateChannel>,
    override val groupChannels: SnowflakeMap<IGroupPrivateChannel>,
    override val unavailableGuilds: SnowflakeMap<UnavailableGuild>,
    override val guilds: SnowflakeMap<IGuild>,
    override val messages: SnowflakeMap<IMessage>,
    override val lastTypes: SnowflakeMap<SnowflakeMap<Instant>>,
    override val users: SnowflakeMap<IUser>,
    override val bans: SnowflakeMap<SnowflakeMap<IBan>>
) : CacheSnapshot {
    companion object {
        fun empty() = MemoryCacheSnapshot(
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap()
        )
    }
}