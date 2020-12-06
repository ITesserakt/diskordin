package org.tesserakt.diskordin.core.data.json.request


data class MemberAddRequest(
    val access_token: String,
    val nick: String? = null,
    val roles: List<Long>? = null,
    val mute: Boolean? = null,
    val deaf: Boolean? = null
) : JsonRequest()
