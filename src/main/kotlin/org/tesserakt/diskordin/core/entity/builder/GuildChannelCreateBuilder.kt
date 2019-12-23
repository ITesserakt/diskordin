@file:Suppress("unused")

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.ChannelCreateRequest
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.IVoiceChannel
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.util.enums.not

@RequestBuilder
@Suppress("NOTHING_TO_INLINE")
abstract class GuildChannelCreateBuilder<C : IGuildChannel>(protected val name: String) :
    AuditLogging<ChannelCreateRequest>() {
    protected abstract val type: IChannel.Type

    protected var position: Int? = null
    protected var permissionOverwrite: IPermissionOverwrite? = null
    protected var parentId: Snowflake? = null


    operator fun Position.unaryPlus() {
        position = this.v
    }

    operator fun IPermissionOverwrite.unaryPlus() {
        permissionOverwrite = this
    }

    operator fun Snowflake.unaryPlus() {
        parentId = this
    }

    inline fun GuildChannelCreateBuilder<*>.position(pos: Int) = Position(pos)
    inline fun GuildChannelCreateBuilder<*>.overwrite(permOverwrite: IPermissionOverwrite) = permOverwrite
    inline fun GuildChannelCreateBuilder<*>.parent(parentId: Snowflake) = parentId
}

@RequestBuilder
@Suppress("NOTHING_TO_INLINE")
class TextChannelCreateBuilder(name: String) : GuildChannelCreateBuilder<ITextChannel>(name) {
    override val type: IChannel.Type = IChannel.Type.GuildText
    private var topic: String? = null
    private var rateLimitPerUser: Int? = null
    private var isNsfw: Boolean? = null

    operator fun String.unaryPlus() {
        topic = this
    }

    operator fun RateLimit.unaryPlus() {
        rateLimitPerUser = this.v
    }

    operator fun Boolean.unaryPlus() {
        isNsfw = this
    }

    inline fun TextChannelCreateBuilder.topic(topic: String) = topic
    inline fun TextChannelCreateBuilder.rateLimitPerUser(rateLimit: Int) = RateLimit(rateLimit)
    inline fun TextChannelCreateBuilder.nsfw(isNsfw: Boolean) = isNsfw

    override fun create(): ChannelCreateRequest = ChannelCreateRequest(
        name,
        type.ordinal,
        topic,
        rate_limit_per_user = rateLimitPerUser,
        position = position,
        permission_overwrites = permissionOverwrite?.let { (it.allowed and !it.denied).code },
        parent_id = parentId,
        nsfw = isNsfw
    )
}

@RequestBuilder
@Suppress("NOTHING_TO_INLINE")
class VoiceChannelCreateBuilder(name: String) : GuildChannelCreateBuilder<IVoiceChannel>(name) {
    override val type: IChannel.Type = IChannel.Type.GuildVoice
    private var bitrate: Int? = null
    private var userLimit: Int? = null

    operator fun Bitrate.unaryPlus() {
        bitrate = this.v
    }

    operator fun UserLimit.unaryPlus() {
        userLimit = this.v
    }

    inline fun VoiceChannelCreateBuilder.bitrate(value: Int) = Bitrate(value)
    inline fun VoiceChannelCreateBuilder.userLimit(limit: Int) = UserLimit(limit)

    override fun create(): ChannelCreateRequest = ChannelCreateRequest(
        name,
        type.ordinal,
        bitrate = bitrate,
        user_limit = userLimit,
        position = position,
        permission_overwrites = permissionOverwrite?.let { (it.allowed and !it.denied).code },
        parent_id = parentId
    )
}