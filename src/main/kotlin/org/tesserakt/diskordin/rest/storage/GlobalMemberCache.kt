package org.tesserakt.diskordin.rest.storage

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IMember
import java.util.concurrent.ConcurrentHashMap

private typealias GuildId = Snowflake
private typealias UserId = Snowflake

object GlobalMemberCache : MutableMap<Pair<GuildId, UserId>, IMember> by ConcurrentHashMap()