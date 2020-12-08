package org.tesserakt.diskordin.core.cache

import arrow.core.extensions.map.functor.map
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild

typealias SnowflakeMutableMap<V> = MutableMap<Snowflake, V>

class CacheSnapshotBuilder private constructor(
    override val privateChannels: SnowflakeMutableMap<IPrivateChannel>,
    override val groupChannels: SnowflakeMutableMap<IGroupPrivateChannel>,
    override val unavailableGuilds: SnowflakeMutableMap<UnavailableGuild>,
    override val guilds: SnowflakeMutableMap<IGuild>,
    override val messages: SnowflakeMutableMap<IMessage>,
    override val lastTypes: SnowflakeMap<SnowflakeMap<Instant>>,
    override val users: SnowflakeMutableMap<IUser>,
    override val bans: SnowflakeMutableMap<SnowflakeMutableMap<IBan>>
) : CacheSnapshot {
    fun immutable() = MemoryCacheSnapshot(
        privateChannels,
        groupChannels,
        unavailableGuilds,
        guilds,
        messages,
        lastTypes,
        users,
        bans
    )

    companion object {
        fun CacheSnapshot.mutate() = if (this is CacheSnapshotBuilder) this else CacheSnapshotBuilder(
            privateChannels.toMutableMap(),
            groupChannels.toMutableMap(),
            unavailableGuilds.toMutableMap(),
            guilds.toMutableMap(),
            messages.toMutableMap(),
            lastTypes.map { it.toMutableMap() }.toMutableMap(),
            users.toMutableMap(),
            bans.map { it.toMutableMap() }.toMutableMap()
        )
    }
}