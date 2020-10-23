package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake

data class GuildCreateRequest(
    val name: String,
    val region: String? = null,
    val icon: String? = null,
    val verification_level: Int? = null,
    val default_message_notifications: Int? = null,
    val explicit_content_filter: Int? = null,
    val roles: List<GuildRoleCreateRequest> = emptyList(),
    val channels: List<PartialChannelCreateRequest> = emptyList(),
    val afkChannelId: Snowflake? = null,
    val afkTimeout: Int? = null,
    val systemChannelId: Snowflake? = null
) : JsonRequest()