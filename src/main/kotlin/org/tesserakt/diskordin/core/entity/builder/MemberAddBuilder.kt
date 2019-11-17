package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.MemberAddRequest

class MemberAddBuilder : BuilderBase<MemberAddRequest>() {
    lateinit var accessToken: String
    var nick: String? = null
    var initialRoles: Array<Snowflake>? = null
    var isMuted: Boolean? = null
    var isDeafen: Boolean? = null

    override fun create(): MemberAddRequest = MemberAddRequest(
        accessToken,
        nick,
        initialRoles?.map { it.asLong() }?.toTypedArray(),
        isMuted,
        isDeafen
    )
}
