package org.tesserakt.diskordin.impl.core.entity

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.GuildPreviewResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.ICustomEmoji
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IGuildPreview
import org.tesserakt.diskordin.core.entity.client

class GuildPreview(raw: GuildPreviewResponse) : IGuildPreview {
    override val id: Snowflake = raw.id
    override val name: String = raw.name
    override val icon: String? = raw.icon
    override val splash: String? = raw.splash
    override val discoverySplash: String? = raw.discoverySplash
    override val emojis: List<ICustomEmoji> = raw.emojis.map { it.unwrap(id) }
    override val features: List<IGuild.Feature> = raw.features.map { IGuild.Feature.valueOf(it) }
    override val approximateMemberCount: Int = raw.approximateMemberCount
    override val approximatePresenceCount: Int = raw.approximatePresenceCount
    override val description: String? = raw.description

    override suspend fun extendToGuild(): IGuild = client.getGuild(id)

    override fun toString(): String {
        return "GuildPreview(icon=$icon, splash=$splash, discoverySplash=$discoverySplash, emojis=$emojis, features=$features, approximateMemberCount=$approximateMemberCount, approximatePresenceCount=$approximatePresenceCount, description=$description, id=$id, name='$name')"
    }
}