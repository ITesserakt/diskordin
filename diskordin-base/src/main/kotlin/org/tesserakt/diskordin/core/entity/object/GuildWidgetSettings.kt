package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.builder.GuildWidgetEditBuilder

interface IGuildWidgetSettings : IDiscordObject {
    val isEnabled: Boolean
    val channel: DeferredIdentified<IGuildChannel>?

    suspend fun edit(guildId: Snowflake, builder: GuildWidgetEditBuilder.() -> Unit): IGuildWidgetSettings
}