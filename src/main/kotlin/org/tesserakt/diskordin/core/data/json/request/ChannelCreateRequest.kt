package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake


data class ChannelCreateRequest(
    val name: String,
    val type: Int? = null,
    val topic: String? = null,
    val bitrate: Int? = null,
    val user_limit: Int? = null,
    val rate_limit_per_user: Int? = null,
    val position: Int? = null,
    val permission_overwrites: Long? = null,
    val parent_id: Snowflake? = null,
    val nsfw: Boolean? = null
) : JsonRequest()