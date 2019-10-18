package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import ru.tesserakt.diskordin.gateway.json.IRawEvent

class MemberJoin(
    val member: GuildMemberResponse,
    val guildId: Snowflake
) : IRawEvent
