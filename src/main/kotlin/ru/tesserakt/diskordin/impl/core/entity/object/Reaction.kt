package ru.tesserakt.diskordin.impl.core.entity.`object`


import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.json.response.ReactionResponse
import ru.tesserakt.diskordin.core.entity.IEmoji
import ru.tesserakt.diskordin.core.entity.`object`.IReaction
import ru.tesserakt.diskordin.impl.core.entity.Emoji

class Reaction(raw: ReactionResponse) : IReaction {
    override val count: Int = raw.count

    override val selfReacted: Boolean = raw.me

    override val emoji: IEmoji = Emoji(raw.emoji)


}