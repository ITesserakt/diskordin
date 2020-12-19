package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class MemberChunk(
    val guildId: Snowflake,
    val members: List<GuildMemberResponse>,
    val notFound: List<Snowflake>? = null
) : IRawEvent
