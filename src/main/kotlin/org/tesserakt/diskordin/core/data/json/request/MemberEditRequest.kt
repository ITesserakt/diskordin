package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake


data class MemberEditRequest(
    val nick: String? = null,
    val roles: List<Long>? = null,
    val mute: Boolean? = null,
    val deaf: Boolean? = null,
    val channel_id: Snowflake? = null
) : JsonRequest()
