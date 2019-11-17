package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import org.tesserakt.diskordin.gateway.json.IRawEvent

class MemberJoin(
    val member: GuildMemberResponse,
    val guildId: Snowflake
) : IRawEvent
