package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.entity.IMessage

internal val MessageUpdater = CacheUpdater<IMessage> { builder, data ->
    builder.copy(messages = builder.messages + (data.id to data))
}

internal val MessageDeleter = CacheDeleter<IMessage> { builder, data ->
    builder.copy(messages = builder.messages - data.id)
}