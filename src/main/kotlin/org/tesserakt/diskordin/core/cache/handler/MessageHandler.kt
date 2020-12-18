package org.tesserakt.diskordin.core.cache.handler

import org.tesserakt.diskordin.core.entity.IMessage

internal val MessageUpdater = CacheUpdater<IMessage> { builder, data ->
    builder.messages += (data.id to data)
}

internal val MessageDeleter = CacheDeleter<IMessage> { builder, data ->
    builder.messages -= data
}