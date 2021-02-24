package org.tesserakt.diskordin.commands

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.entity.*

interface CommandContext : IDiscordObject {
    val message: EagerIdentified<IMessage>
    val author: EagerIdentified<IUser>
    val commandArgs: Array<String>
    val channel: DeferredIdentified<IMessageChannel>
}

interface GuildCommandContext : CommandContext {
    val guild: DeferredIdentified<IGuild>
    override val channel: DeferredIdentified<ITextChannel>
    suspend fun authorAsMember(): DeferredIdentified<IMember> {
        val author = author()
        return author.id deferred { author.asMember(guild.id) }
    }
}

interface DMCommandContext : CommandContext {
    override val channel: DeferredIdentified<IPrivateChannel>
}