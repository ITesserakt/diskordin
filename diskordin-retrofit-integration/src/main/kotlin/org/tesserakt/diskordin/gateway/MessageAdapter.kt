package org.tesserakt.diskordin.gateway

import com.tinder.scarlet.MessageAdapter
import com.tinder.scarlet.utils.getRawType
import org.tesserakt.diskordin.gateway.json.Message
import java.lang.reflect.Type

object MessageAdapter : MessageAdapter<Message> {
    override fun fromMessage(message: com.tinder.scarlet.Message): Message = message()
    override fun toMessage(data: Message): com.tinder.scarlet.Message = data()

    object Factory : MessageAdapter.Factory {
        override fun create(type: Type, annotations: Array<Annotation>): MessageAdapter<*> = when (type.getRawType()) {
            Message::class.java -> MessageAdapter
            else -> throw IllegalArgumentException("No adapter for type $type")
        }
    }
}