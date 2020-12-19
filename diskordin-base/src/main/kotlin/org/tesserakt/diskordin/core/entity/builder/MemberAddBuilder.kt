package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.MemberAddRequest

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class MemberAddBuilder(private val accessToken: String) : BuilderBase<MemberAddRequest>() {
    private var nick: String? = null
    private val initialRoles: MutableList<Snowflake> = mutableListOf()
    private var isMuted: Boolean? = null
    private var isDeafen: Boolean? = null

    operator fun String.unaryPlus() {
        nick = this
    }

    operator fun Snowflake.unaryPlus() {
        initialRoles += this
    }

    operator fun Muted.unaryPlus() {
        isMuted = this.v
    }

    operator fun Boolean.unaryPlus() {
        isDeafen = this
    }

    inline fun MemberAddBuilder.nick(nick: String) = nick
    inline fun MemberAddBuilder.role(id: Snowflake) = id
    inline fun MemberAddBuilder.muted(value: Boolean) = Muted(value)
    inline fun MemberAddBuilder.deafen(value: Boolean) = value

    override fun create(): MemberAddRequest = MemberAddRequest(
        accessToken,
        nick,
        initialRoles,
        isMuted,
        isDeafen
    )
}
