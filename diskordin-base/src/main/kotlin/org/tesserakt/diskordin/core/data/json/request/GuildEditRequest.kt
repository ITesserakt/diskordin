package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake


data class GuildEditRequest(
    val name: String? = null,
    val region: String? = null,
    val verification_level: Int? = null,
    val default_message_notifications: Int? = null,
    val explicit_content_filter: Int? = null,
    val afk_channel_id: Snowflake? = null,
    val afk_timeout: Int? = null,
    val icon: String? = null,
    val owner_id: Snowflake? = null,
    val splash: String? = null,
    val system_channel_id: Snowflake? = null
) : JsonRequest()
