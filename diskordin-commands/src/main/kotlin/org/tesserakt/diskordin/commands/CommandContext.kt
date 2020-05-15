package org.tesserakt.diskordin.commands

import arrow.core.extensions.id.comonad.extract
import arrow.fx.ForIO
import arrow.fx.typeclasses.Async
import org.tesserakt.diskordin.commands.util.fromIO
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.entity.*

interface CommandContext<F> : IDiscordObject {
    val message: Identified<IMessage>
    val author: Identified<IUser>
    val commandArgs: Array<String>
    val channel: IdentifiedF<F, IMessageChannel>
}

interface GuildCommandContext<F> : CommandContext<F> {
    val guild: IdentifiedF<ForIO, IGuild>
    override val channel: IdentifiedF<F, ITextChannel>
    fun authorAsMember(A: Async<F>): IdentifiedF<F, IMember> =
        author.map { it.extract().asMember(guild.state).fromIO(A) }
}

interface DMCommandContext<F> : CommandContext<F> {
    override val channel: IdentifiedF<F, IPrivateChannel>
}