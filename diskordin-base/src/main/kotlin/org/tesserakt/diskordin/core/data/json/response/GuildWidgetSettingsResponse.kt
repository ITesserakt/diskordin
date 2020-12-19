package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.`object`.IGuildWidgetSettings
import org.tesserakt.diskordin.impl.core.entity.`object`.GuildWidgetSettings

data class GuildWidgetSettingsResponse(
    val enabled: Boolean,
    val channelId: Snowflake?
) : DiscordResponse<IGuildWidgetSettings, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IGuildWidgetSettings = GuildWidgetSettings(this)
}