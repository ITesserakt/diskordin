package org.tesserakt.diskordin.commands

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.*
import org.tesserakt.diskordin.core.entity.*

interface CommandContext : IDiscordObject {
    val message: Identified<IMessage>
    val author: Identified<IUser>
    val commandArgs: Array<String>
    val channel: IdentifiedIO<IMessageChannel>
}

interface GuildCommandContext : CommandContext {
    val guild: IdentifiedF<ForIO, IGuild>
    override val channel: IdentifiedIO<ITextChannel>
    suspend fun authorAsMember(): IdentifiedIO<IMember> {
        val author = author()
        return author.id.identify<IMember> { author.asMember(guild.id) }
    }
}

interface DMCommandContext : CommandContext {
    override val channel: IdentifiedIO<IPrivateChannel>
}