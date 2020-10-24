package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.ICustomEmoji
import org.tesserakt.diskordin.core.entity.IGuildPreview
import org.tesserakt.diskordin.impl.core.entity.GuildPreview

data class GuildPreviewResponse(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val splash: String?,
    val discoverySplash: String?,
    val emojis: List<EmojiResponse<ICustomEmoji>>,
    val features: List<String>,
    val approximateMemberCount: Int,
    val approximatePresenceCount: Int,
    val description: String?
) : DiscordResponse<IGuildPreview, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IGuildPreview = GuildPreview(this)
}