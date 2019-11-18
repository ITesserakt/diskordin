package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.ChannelEditRequest
import org.tesserakt.diskordin.core.data.json.response.OverwriteResponse
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.IVoiceChannel

abstract class GuildChannelEditBuilder<T : IGuildChannel> : AuditLogging<ChannelEditRequest>() {
    var name: String? = null
    var position: Int? = null
    override var reason: String? = null
    var permissionOverwrites: Array<OverwriteResponse>? = null

    var parentId: Snowflake? = null
}

class TextChannelEditBuilder : GuildChannelEditBuilder<ITextChannel>() {
    var topic: String? = null
    var isNsfw: Boolean? = null
    var rateLimit: Long? = null


    override fun create(): ChannelEditRequest = ChannelEditRequest(
        name,
        position,
        topic,
        isNsfw,
        rateLimit,
        permission_overwrites = permissionOverwrites,
        parent_id = parentId
    )
}

class VoiceChannelEditBuilder : GuildChannelEditBuilder<IVoiceChannel>() {
    var bitrate: Int? = null
    var userLimit: Int? = null


    override fun create(): ChannelEditRequest = ChannelEditRequest(
        name,
        position,
        permission_overwrites = permissionOverwrites,
        parent_id = parentId,
        bitrate = bitrate,
        user_limit = userLimit
    )
}