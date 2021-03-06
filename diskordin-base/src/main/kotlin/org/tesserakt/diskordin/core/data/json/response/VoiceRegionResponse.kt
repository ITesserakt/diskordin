package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.impl.core.entity.`object`.Region


data class VoiceRegionResponse(
    val id: String,
    val name: String,
    val vip: Boolean,
    val optimal: Boolean,
    val deprecated: Boolean,
    val custom: Boolean
) : DiscordResponse<IRegion, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IRegion = Region(this)
}
