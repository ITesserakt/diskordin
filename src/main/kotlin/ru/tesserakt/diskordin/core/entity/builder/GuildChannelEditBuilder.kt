package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.ChannelEditRequest
import ru.tesserakt.diskordin.core.data.json.response.OverwriteResponse
import ru.tesserakt.diskordin.core.entity.IGuildChannel
import ru.tesserakt.diskordin.core.entity.ITextChannel
import ru.tesserakt.diskordin.core.entity.IVoiceChannel

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
        parent_id = parentId?.asLong()
    )
}

class VoiceChannelEditBuilder : GuildChannelEditBuilder<IVoiceChannel>() {
    var bitrate: Int? = null
    var userLimit: Int? = null


    override fun create(): ChannelEditRequest = ChannelEditRequest(
        name,
        position,
        permission_overwrites = permissionOverwrites,
        parent_id = parentId?.asLong(),
        bitrate = bitrate,
        user_limit = userLimit
    )
}