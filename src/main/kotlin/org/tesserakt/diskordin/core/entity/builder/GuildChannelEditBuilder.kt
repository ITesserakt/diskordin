@file:Suppress("unused")

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.ChannelEditRequest
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.IVoiceChannel
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite

@RequestBuilder
@Suppress("NOTHING_TO_INLINE")
abstract class GuildChannelEditBuilder<T : IGuildChannel> : AuditLogging<ChannelEditRequest>() {
    protected var name: String? = null
    protected var position: Int? = null
    protected var permissionOverwrite: IPermissionOverwrite? = null
    protected var parentId: Snowflake? = null

    operator fun Name.unaryPlus() {
        name = this.v
    }


    operator fun Position.unaryPlus() {
        position = this.v
    }

    operator fun IPermissionOverwrite.unaryPlus() {
        permissionOverwrite = this
    }

    operator fun Snowflake.unaryPlus() {
        parentId = this
    }

    inline fun GuildChannelEditBuilder<*>.name(name: String) = Name(name)
    inline fun GuildChannelEditBuilder<*>.position(pos: Int) = Position(pos)
    inline fun GuildChannelEditBuilder<*>.overwrite(permOverwrite: IPermissionOverwrite) = permOverwrite
    inline fun GuildChannelEditBuilder<*>.parent(parentId: Snowflake) = parentId
}

@RequestBuilder
@Suppress("NOTHING_TO_INLINE")
class TextChannelEditBuilder : GuildChannelEditBuilder<ITextChannel>() {
    private var topic: String? = null
    private var isNsfw: Boolean? = null
    private var rateLimitPerUser: Int? = null

    operator fun String.unaryPlus() {
        topic = this
    }


    operator fun RateLimit.unaryPlus() {
        rateLimitPerUser = this.v
    }

    operator fun Boolean.unaryPlus() {
        isNsfw = this
    }

    inline fun TextChannelEditBuilder.topic(topic: String) = topic
    inline fun TextChannelEditBuilder.rateLimitPerUser(rateLimit: Int) = RateLimit(rateLimit)
    inline fun TextChannelEditBuilder.nsfw(isNsfw: Boolean) = isNsfw

    override fun create(): ChannelEditRequest = ChannelEditRequest(
        name,
        position,
        topic,
        isNsfw,
        rateLimitPerUser,
        permission_overwrites = permissionOverwrite?.computeCode(),
        parent_id = parentId
    )
}

@RequestBuilder
@Suppress("NOTHING_TO_INLINE")
class VoiceChannelEditBuilder : GuildChannelEditBuilder<IVoiceChannel>() {
    private var bitrate: Int? = null
    private var userLimit: Int? = null

    operator fun Bitrate.unaryPlus() {
        bitrate = this.v
    }

    operator fun UserLimit.unaryPlus() {
        userLimit = this.v
    }

    inline fun VoiceChannelEditBuilder.bitrate(value: Int) = Bitrate(value)
    inline fun VoiceChannelEditBuilder.userLimit(limit: Int) = UserLimit(limit)

    override fun create(): ChannelEditRequest = ChannelEditRequest(
        name,
        position,
        permission_overwrites = permissionOverwrite?.computeCode(),
        parent_id = parentId,
        bitrate = bitrate,
        user_limit = userLimit
    )
}