package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.entity.IPrivateChannel

internal val PrivateChannelUpdater = CacheUpdater<IPrivateChannel> { builder, data ->
    builder.privateChannels[data.id] = data
}

internal val PrivateChannelDeleter = CacheDeleter<IPrivateChannel> { builder, data ->
    builder.privateChannels -= data
}

