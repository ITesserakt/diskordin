package org.tesserakt.diskordin.commands

import arrow.core.Ior
import org.tesserakt.diskordin.core.entity.builder.Content
import org.tesserakt.diskordin.core.entity.builder.Embed
import org.tesserakt.diskordin.core.entity.builder.MessageCreateBuilder

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
abstract class CommandModule<in C : CommandContext> {
    protected suspend fun C.reply(content: String) = channel().createMessage(content)

    protected suspend fun C.reply(required: Ior<Content, Embed>, builder: MessageCreateBuilder.() -> Unit = {}) =
        channel().createMessage(required, builder)

    interface Factory {
        fun create(): CommandModule<*>
    }
}