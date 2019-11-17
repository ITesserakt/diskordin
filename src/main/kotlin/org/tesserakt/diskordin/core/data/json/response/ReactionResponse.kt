package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.entity.`object`.IReaction
import org.tesserakt.diskordin.impl.core.entity.`object`.Reaction


data class ReactionResponse(
    val count: Int,
    val me: Boolean,
    val emoji: EmojiResponse<*>
) : DiscordResponse<IReaction, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IReaction = Reaction(this)
}
