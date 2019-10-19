package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.MemberEditRequest

class MemberEditBuilder : AuditLogging<MemberEditRequest>() {
    override fun create(): MemberEditRequest = MemberEditRequest(
        nick, roles?.map { it.asLong() }?.toTypedArray(), isMuted, isDeafen, channelId
    )

    var nick: String? = null
    var roles: Array<Snowflake>? = null
    var isMuted: Boolean? = null
    var isDeafen: Boolean? = null
    var channelId: Snowflake? = null
    override var reason: String? = null
}