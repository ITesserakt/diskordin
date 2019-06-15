package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IGuildChannel

abstract class GuildChannelCreateBuilder<C : IGuildChannel> : AuditLogging<ChannelCreateRequest>() {
    lateinit var name: String
    protected abstract val type: IChannel.Type
    override var reason: String? = null
    var position: Int? = null
    var permissionOverwrites: Array<OverwriteResponse>? = null

    var parentId: Snowflake? = null
}

class TextChannelCreateBuilder : GuildChannelCreateBuilder<TextChannel>() {
    override val type: IChannel.Type = IChannel.Type.GuildText
    var topic: String? = null
    var rateLimitPerUser: Int? = null
    var isNsfw: Boolean? = null

    override fun create(): ChannelCreateRequest = ChannelCreateRequest(
        name,
        type.ordinal,
        topic,
        rate_limit_per_user = rateLimitPerUser,
        position = position,
        permission_overwrites = permissionOverwrites,
        parent_id = parentId?.asLong(),
        nsfw = isNsfw
    )
}

class VoiceChannelCreateBuilder : GuildChannelCreateBuilder<VoiceChannel>() {
    override val type: IChannel.Type = IChannel.Type.GuildVoice
    var bitrate: Int? = null
    var userLimit: Int? = null

    override fun create(): ChannelCreateRequest = ChannelCreateRequest(
        name,
        type.ordinal,
        bitrate = bitrate,
        user_limit = userLimit,
        position = position,
        permission_overwrites = permissionOverwrites,
        parent_id = parentId?.asLong()
    )
}