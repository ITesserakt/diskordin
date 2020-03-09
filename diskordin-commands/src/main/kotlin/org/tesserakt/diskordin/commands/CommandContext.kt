package org.tesserakt.diskordin.commands

import arrow.Kind
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IMessage
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.IUser

interface CommandContext<F> : IDiscordObject {
    val message: IMessage
    val author: IUser
    val commandArgs: Array<String>
    val channel: Kind<F, IMessageChannel>
}