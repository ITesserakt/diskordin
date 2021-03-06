package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.json.response.ReactionResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IEmoji
import org.tesserakt.diskordin.core.entity.`object`.IReaction

internal class Reaction(raw: ReactionResponse) : IReaction {
    override val count: Int = raw.count

    override val selfReacted: Boolean = raw.me

    override val emoji: IEmoji = raw.emoji.unwrap()

    override fun toString(): String {
        return "Reaction(count=$count, selfReacted=$selfReacted, emoji=$emoji)"
    }
}