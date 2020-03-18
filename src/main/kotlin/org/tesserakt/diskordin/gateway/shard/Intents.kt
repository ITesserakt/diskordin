package org.tesserakt.diskordin.gateway.shard

import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.IValued
import org.tesserakt.diskordin.util.typeclass.Integral

enum class Intents(override val value: Short) : IValued<Intents, Short>, Integral<Short> by Short.integral() {
    Guilds(1 shl 0),
    GuildMembers(1 shl 1),
    GuildBans(1 shl 2),
    GuildEmojis(1 shl 3),
    GuildIntegrations(1 shl 4),
    GuildWebhooks(1 shl 5),
    GuildInvites(1 shl 6),
    GuildVoiceStates(1 shl 7),
    GuildPresences(1 shl 8),
    GuildMessages(1 shl 9),
    GuildMessageReactions(1 shl 10),
    GuildMessageTyping(1 shl 11),
    DirectMessages(1 shl 12),
    DirectMessageReactions(1 shl 13),
    DirectMessageTyping(1 shl 14)
}