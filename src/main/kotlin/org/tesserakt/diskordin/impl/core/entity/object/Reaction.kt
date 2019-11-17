package org.tesserakt.diskordin.impl.core.entity.`object`


import arrow.core.none
import org.tesserakt.diskordin.core.data.json.response.ReactionResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IEmoji
import org.tesserakt.diskordin.core.entity.`object`.IReaction

class Reaction(raw: ReactionResponse) : IReaction {
    override val count: Int = raw.count

    override val selfReacted: Boolean = raw.me

    override val emoji: IEmoji = raw.emoji.unwrap(none())

    override fun toString(): String {
        return StringBuilder("Reaction(")
            .appendln("count=$count, ")
            .appendln("selfReacted=$selfReacted, ")
            .appendln("emoji=$emoji")
            .appendln(")")
            .toString()
    }
}