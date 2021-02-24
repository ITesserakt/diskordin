package org.tesserakt.diskordin.core.cache

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild
import java.util.concurrent.ConcurrentHashMap

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
            ConcurrentHashMap(privateChannels),
            ConcurrentHashMap(groupChannels),
            ConcurrentHashMap(unavailableGuilds),
            ConcurrentHashMap(guilds),
            ConcurrentHashMap(messages),
            lastTypes.mapValues { (_, a) -> ConcurrentHashMap(a) }.toMap(ConcurrentHashMap()),
            ConcurrentHashMap(users),
            bans.mapValues { (_, a) -> ConcurrentHashMap(a) }.toMap(ConcurrentHashMap())
        )
    }
}