package org.tesserakt.diskordin.core.cache.handler

import org.tesserakt.diskordin.core.cache.CacheSnapshotBuilder
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.impl.core.entity.*

internal val PrivateChannelUpdater = CacheUpdater<IPrivateChannel> { builder, data ->
    builder.privateChannels[data.id] = data
}

internal val PrivateChannelDeleter = CacheDeleter<IPrivateChannel> { builder, data ->
    builder.privateChannels -= data
}

internal val GroupPrivateChannelUpdater = CacheUpdater<IGroupPrivateChannel> { builder, data ->
    builder.groupChannels[data.id] = data
}

internal val GroupPrivateChannelDeleter = CacheDeleter<IGroupPrivateChannel> { builder, id ->
    builder.groupChannels -= id
}

private inline val GuildChannel.raw
    get() = when (this) {
        is TextChannel -> raw
        is VoiceChannel -> raw
        is Category -> raw
        is AnnouncementChannel -> raw
    }

internal val GuildChannelUpdater = CacheUpdater<IGuildChannel> { builder, data ->
    val guild = builder.getGuild(data.guild.id) ?: return@CacheUpdater
    data as GuildChannel

    builder.guilds[guild.id] = when (guild) {
        is Guild -> guild.copy {
            it.copy(channels = it.channels + data.raw)
        }
        is PartialGuild -> guild.copy {
            it.copy(channels = it.channels + data.raw)
        }
        else -> guild
    }
}

internal val GuildChannelDeleter = CacheDeleter<IGuildChannel> { builder, id ->
    val channel = builder.getGuildChannel(id) as? GuildChannel ?: return@CacheDeleter
    val guild = channel.guild.id.let { builder.getGuild(it) } ?: return@CacheDeleter

    builder.guilds[guild.id] = when (guild) {
        is Guild -> guild.copy {
            it.copy(channels = it.channels - channel.raw)
        }
        is PartialGuild -> guild.copy {
            it.copy(channels = it.channels - channel.raw)
        }
        else -> guild
    }
}

@Suppress("UNCHECKED_CAST")
internal val ChannelUpdater = CacheUpdater<IChannel> { builder, data ->
    when (data) {
        is IGuildChannel -> GuildChannelUpdater
        is IPrivateChannel -> PrivateChannelUpdater
        is IGroupPrivateChannel -> GroupPrivateChannelUpdater
        else -> NoopHandler
    }.let {
        it as CacheHandler<IChannel>
        it.handle(builder, data)
    }
}

@Suppress("UNCHECKED_CAST")
internal val ChannelDeleter = object : CacheDeleter<IChannel> {
    override fun handle(builder: CacheSnapshotBuilder, data: IChannel) = when (data) {
        is IGuildChannel -> GuildChannelDeleter
        is IPrivateChannel -> PrivateChannelDeleter
        is IGroupPrivateChannel -> GroupPrivateChannelDeleter
        else -> NoopHandler
    }.let {
        it as CacheHandler<IChannel>
        it.handle(builder, data)
    }

    override fun delete(builder: CacheSnapshotBuilder, id: Snowflake) = builder.getChannel(id)?.let {
        handle(builder, it)
    } ?: Unit
}