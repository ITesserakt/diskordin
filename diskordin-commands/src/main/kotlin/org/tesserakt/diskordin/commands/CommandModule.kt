package org.tesserakt.diskordin.commands

import arrow.core.Ior
import arrow.fx.typeclasses.Async
import org.tesserakt.diskordin.commands.util.fromIO
import org.tesserakt.diskordin.core.entity.builder.Content
import org.tesserakt.diskordin.core.entity.builder.Embed
import org.tesserakt.diskordin.core.entity.builder.MessageCreateBuilder

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
abstract class CommandModule<F, C : CommandContext<F>>(A: Async<F>) : Async<F> by A {
    protected fun C.reply(content: String) = channel.flatMap {
        it.createMessage(content).fromIO(this@CommandModule)
    }

    protected fun C.reply(required: Ior<Content, Embed>, builder: MessageCreateBuilder.() -> Unit) = channel.flatMap {
        it.createMessage(required, builder).fromIO(this@CommandModule)
    }
}