package org.tesserakt.diskordin.core.cache

import arrow.core.extensions.map.functor.map
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild
import java.time.Instant

typealias SnowflakeMutableMap<V> = MutableMap<Snowflake, V>

class CacheSnapshotBuilder(
    override val privateChannels: SnowflakeMutableMap<IPrivateChannel>,
    override val groupChannels: SnowflakeMutableMap<IGroupPrivateChannel>,
    override val unavailableGuilds: SnowflakeMutableMap<UnavailableGuild>,
    override val guilds: SnowflakeMutableMap<IGuild>,
    override val messages: SnowflakeMutableMap<IMessage>,
    override val lastTypes: SnowflakeMutableMap<SnowflakeMutableMap<Instant>>,
    override val users: SnowflakeMutableMap<IUser>,
    override val bans: SnowflakeMutableMap<SnowflakeMutableMap<IBan>>
) : CacheSnapshot {
    fun toImmutable() = MemoryCacheSnapshot(
        privateChannels,
        groupChannels,
        unavailableGuilds,
        guilds,
        messages,
        lastTypes,
        users,
        bans
    )

    fun copy() = CacheSnapshotBuilder(toImmutable())

    companion object {
        operator fun invoke(inner: MemoryCacheSnapshot) = CacheSnapshotBuilder(
            inner.privateChannels.toMutableMap(),
            inner.groupChannels.toMutableMap(),
            inner.unavailableGuilds.toMutableMap(),
            inner.guilds.toMutableMap(),
            inner.messages.toMutableMap(),
            inner.lastTypes.map { it.toMutableMap() }.toMutableMap(),
            inner.users.toMutableMap(),
            inner.bans.map { it.toMutableMap() }.toMutableMap()
        )
    }
}