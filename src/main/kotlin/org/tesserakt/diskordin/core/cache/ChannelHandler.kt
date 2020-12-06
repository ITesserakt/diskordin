package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.entity.IPrivateChannel

internal val PrivateChannelUpdater = CacheUpdater<IPrivateChannel> { builder, data ->
    builder.copy(privateChannels = builder.privateChannels + (data.id to data))
}

internal val PrivateChannelDeleter = CacheDeleter<IPrivateChannel> { builder, data ->
    builder.copy(privateChannels = builder.privateChannels - data.id)
}

