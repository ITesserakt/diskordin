package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.json.response.GuildWidgetSettingsResponse
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.`object`.IGuildWidgetSettings
import org.tesserakt.diskordin.core.entity.builder.GuildWidgetEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.core.entity.client

class GuildWidgetSettings(raw: GuildWidgetSettingsResponse) : IGuildWidgetSettings {
    override val isEnabled: Boolean = raw.enabled
    override val channel: DeferredIdentified<IGuildChannel>? = raw.channelId?.deferred {
        client.getChannel(it) as IGuildChannel
    }

    override suspend fun edit(guildId: Snowflake, builder: GuildWidgetEditBuilder.() -> Unit): IGuildWidgetSettings =
        client.rest.call {
            guildService.modifyGuildWidget(guildId, builder.build { GuildWidgetEditBuilder(isEnabled) })
        }
}