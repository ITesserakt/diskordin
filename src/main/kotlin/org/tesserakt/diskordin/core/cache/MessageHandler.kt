package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.entity.IMessage

class MessageUpdater : CacheUpdater<IMessage> {
    override fun handle(builder: MemoryCacheSnapshot, data: IMessage): MemoryCacheSnapshot = builder.copy(
        messages = builder.messages + (data.id to data)
    )
}

class MessageDeleter : CacheDeleter<IMessage> {
    override fun handle(builder: MemoryCacheSnapshot, data: IMessage): MemoryCacheSnapshot = builder.copy(
        messages = builder.messages - data.id
    )
}