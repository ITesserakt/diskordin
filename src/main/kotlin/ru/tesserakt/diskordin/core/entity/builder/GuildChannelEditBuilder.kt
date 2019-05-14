package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.ChannelEditRequest
import ru.tesserakt.diskordin.core.data.json.response.OverwriteResponse

abstract class GuildChannelEditBuilder : IAuditLogging<ChannelEditRequest> {
    var name: String? = null
    var position: Int? = null
    override var reason: String? = null
    var permissionOverwrites: Array<OverwriteResponse>? = null

    var parentId: Snowflake? = null
}

class TextChannelEditBuilder : GuildChannelEditBuilder() {
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

class VoiceChannelEditBuilder : GuildChannelEditBuilder() {
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