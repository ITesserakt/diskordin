package ru.tesserakt.diskordin.impl.core.entity.`object`


import arrow.core.none
import ru.tesserakt.diskordin.core.data.json.response.ReactionResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.IEmoji
import ru.tesserakt.diskordin.core.entity.`object`.IReaction

class Reaction(raw: ReactionResponse) : IReaction {
    override val count: Int = raw.count

    override val selfReacted: Boolean = raw.me

    override val emoji: IEmoji = raw.emoji.unwrap(none())

    override fun toString(): String {
        return "Reaction(count=$count, selfReacted=$selfReacted, emoji=$emoji)"
    }
}