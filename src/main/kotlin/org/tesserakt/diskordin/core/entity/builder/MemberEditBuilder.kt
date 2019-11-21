package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.MemberEditRequest

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class MemberEditBuilder : AuditLogging<MemberEditRequest>() {
    override fun create(): MemberEditRequest = MemberEditRequest(
        nick,
        roles?.map { it.asLong() }?.toTypedArray(),
        isMuted,
        isDeafen,
        channelId
    )

    private var nick: String? = null
    private var roles: MutableList<Snowflake>? = null
    private var isMuted: Boolean? = null
    private var isDeafen: Boolean? = null
    private var channelId: Snowflake? = null

    operator fun String.unaryPlus() {
        nick = this
    }

    operator fun Role.unaryPlus() {
        if (roles == null)
            roles = mutableListOf()
        roles!!.plusAssign(this.v)
    }

    operator fun Muted.unaryPlus() {
        isMuted = this.v
    }

    operator fun Boolean.unaryPlus() {
        isDeafen = this
    }

    operator fun Snowflake.unaryPlus() {
        channelId = this
    }

    inline fun MemberEditBuilder.nick(nick: String) = nick
    inline fun MemberEditBuilder.role(id: Snowflake) = Role(id)
    inline fun MemberEditBuilder.muted(value: Boolean) = Muted(value)
    inline fun MemberEditBuilder.deafen(value: Boolean) = value
    inline fun MemberEditBuilder.channel(id: Snowflake) = id
}