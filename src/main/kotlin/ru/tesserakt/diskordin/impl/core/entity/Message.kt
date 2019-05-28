package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse
import ru.tesserakt.diskordin.core.entity.IAttachment
import ru.tesserakt.diskordin.core.entity.IMessage
import ru.tesserakt.diskordin.core.entity.IMessageChannel
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.util.Identified

class Message(raw: MessageResponse, override val kodein: Kodein) : IMessage {
    override val client by instance<IDiscordClient>()

    override val channel: Identified<IMessageChannel> = Identified(raw.channel_id.asSnowflake()) {
        client.coroutineScope.async {
            client.findChannel(it) as IMessageChannel
        }
    }


    override val author: Identified<IUser> = Identified(raw.author.id.asSnowflake()) {
        client.coroutineScope.async {
            User(raw.author, kodein)
        }
    }

    override val content: String = raw.content

    override val isTTS: Boolean = raw.tts

    @FlowPreview
    override val attachments: Flow<IAttachment>? = raw.attachments.map { Attachment(it, kodein) }.asFlow()

    override val isPinned: Boolean = raw.pinned


    override val id: Snowflake = raw.id.asSnowflake()
}