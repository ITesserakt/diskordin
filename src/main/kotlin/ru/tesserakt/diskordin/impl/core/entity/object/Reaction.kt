package ru.tesserakt.diskordin.impl.core.entity.`object`

import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.json.response.ReactionResponse
import ru.tesserakt.diskordin.core.entity.IEmoji
import ru.tesserakt.diskordin.core.entity.`object`.IReaction
import ru.tesserakt.diskordin.impl.core.entity.Emoji

class Reaction(raw: ReactionResponse, override val kodein: Kodein) : IReaction {
    override val count: Int = raw.count

    override val selfReacted: Boolean = raw.me

    override val emoji: IEmoji = Emoji(raw.emoji, kodein)

    override val client: IDiscordClient by instance()
}